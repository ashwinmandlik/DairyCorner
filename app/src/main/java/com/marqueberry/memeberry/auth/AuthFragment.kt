package com.marqueberry.memeberry.auth

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.marqueberry.memeberry.R
import com.marqueberry.memeberry.cache.OfflineStorage
import com.marqueberry.memeberry.cache.OfflineStorage.getProfileData
import com.marqueberry.memeberry.cache.OfflineStorage.isUserLoggedIn
import com.marqueberry.memeberry.databinding.FragmentAuthBinding
import java.util.concurrent.TimeUnit

class AuthFragment : Fragment() {

    private var _binding: FragmentAuthBinding? = null
    private val binding get() = _binding!!
    var disable: Boolean = false
    lateinit var conCode: String
    lateinit var phone: String
    lateinit var phoneCurrentUser: String
    lateinit var resend: TextView
    lateinit var fullNumber: String
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var firestore: FirebaseFirestore

    private var mCollBacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null
    private var mVerificationId: String? = null
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var timer: MyCounter

    private val TAG = "TAGES"
    private lateinit var progressDialog: ProgressDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isUserLoggedIn) {
            findNavController().navigate(R.id.action_authFragment_to_homeActivity)
        } else{
            firebaseAuth = FirebaseAuth.getInstance()

        if (firebaseAuth.currentUser != null && context != null) {

            if (getProfileData(requireContext())) {
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_authFragment_to_homeActivity)
            } else {

            }
        }

        binding.scrollViewPhoneAuth.visibility = View.VISIBLE
        binding.ScrollViewOTP.visibility = View.GONE
        timer = MyCounter(60000, 1000)

        resend = view.findViewById(R.id.resend)
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Please Wait")
        progressDialog.setCanceledOnTouchOutside(false)

        mCollBacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                progressDialog.dismiss()
                binding.phoneBox.error = "Enter Valid Mobile Number"
                binding.phoneBox.requestFocus()
                Log.d("phoness", "${e.message}")
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d(TAG, "onCodeSend : $verificationId")
                mVerificationId = verificationId
                forceResendingToken = token
                progressDialog.dismiss()

                //hide phone Auth scrollView
                binding.scrollViewPhoneAuth.visibility = View.GONE
                binding.ScrollViewOTP.visibility = View.VISIBLE

                Toast.makeText(context, "Code Send", Toast.LENGTH_SHORT).show()
                phoneCurrentUser = binding.phoneBox.text.toString()
                binding.textView5.text = "+91${binding.phoneBox.text}"
                timer.start()
            }
        }

        //Phone Get Otp
        binding.otpBtn.setOnClickListener {
            conCode = binding.conCode.text.toString()
            phone = binding.phoneBox.text.toString()
            fullNumber = "$conCode$phone"
            Log.d("fullNumber", "full number: $fullNumber")
            Log.d("phone", "number: $phone")
            //validate phone number
            if (TextUtils.isEmpty(fullNumber)) {
                Toast.makeText(context, "Phone Nomber", Toast.LENGTH_SHORT)
                    .show()

            } else {
                startPhoneNumberVerification(fullNumber)
            }
        }

        //Resend Code
        binding.resend.setOnClickListener {

            if (disable == true) {
                Log.d("fullNumber", "full number: $fullNumber")
                Log.d("phone", "full number: $phone")
                //validate phone number

                resendVerificationCode(fullNumber, forceResendingToken)
                disable = false
            }
        }


        //Verify
        binding.verifyBtn.setOnClickListener {
            val code = binding.otpView.otp?.trim()
            if (TextUtils.isEmpty(code)) {
                binding.otpView.showError()
                Toast.makeText(context, "Please Enter OTP", Toast.LENGTH_SHORT).show()
            } else {
                verifyPhoneNumberWithCode(mVerificationId, code!!)
            }
        }
    }
}

    private fun startPhoneNumberVerification(fullNumber: String) {
        progressDialog.setMessage("Verifying Phone Number...")
        progressDialog.show()


        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(fullNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(mCollBacks!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun resendVerificationCode(
        fullNumber: String,
        token: PhoneAuthProvider.ForceResendingToken?
    ) {
        progressDialog.setMessage("Resending Code...")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(fullNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(context as Activity)
            .setCallbacks(mCollBacks!!)
            .setForceResendingToken(token!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, code: String) {
        progressDialog.setMessage("Verifying Code...")
        progressDialog.show()

        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithPhoneAuthCredential(credential)

    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        progressDialog.setMessage("Logging In...")


        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                progressDialog.dismiss()
                val phone = firebaseAuth.currentUser!!.phoneNumber
                Toast.makeText(context, "Logged in as $phone", Toast.LENGTH_SHORT).show()
                timer.cancel()

                if (phone != null) {
                    checkIfUserIsExisting(phone)
                }
                //Navigation.findNavController(requireView()).navigate(R.id.profileFragment)
//-------------
//                var ref = FirebaseDatabase.getInstance().getReference("userProfileData")
//                    .child(phoneCurrentUser)
//                ref.addListenerForSingleValueEvent(object : ValueEventListener {
//                    @SuppressLint("ResourceType")
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        if (snapshot.exists()) {
//                            Toast.makeText(context, "$phone exists", Toast.LENGTH_SHORT).show()
//                            Navigation.findNavController(view!!).navigate(R.id.homeFeed)
//                            findNavController().navigate(R.navigation.home_nav)
//
//                        } else {
//                            Toast.makeText(context, "$phone not exists", Toast.LENGTH_SHORT).show()
//
//                                findNavController().navigate(R.id.profileFragment)
//
//                            Navigation.findNavController(view!!).navigate(R.id.authFragment)
//
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        Toast.makeText(context, "Error occured!", Toast.LENGTH_SHORT).show()
//                    }
//                })


                // -----------------------


            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                binding.otpView.showError()
                binding.otpView.requestFocus()
            }
    }

    private fun checkIfUserIsExisting(s:String) {
        firestore= FirebaseFirestore.getInstance()

        firestore.collection("Users").document(s).get()
            .addOnSuccessListener {
                if (it.data==null) {
                    findNavController().navigate(R.id.action_authFragment_to_profileFragment)
                } else {
                    OfflineStorage.setProfileData(requireContext(),true)
                    findNavController().navigate(R.id.action_authFragment_to_homeActivity)

                }


            }
    }


    inner class MyCounter(millisInFuture: Long, countDownInterval: Long) :
        CountDownTimer(millisInFuture, countDownInterval) {

        override fun onFinish() {
            println("Timer Completed.")
            disable = true
            resend.text = "Resend"
        }

        override fun onTick(millisUntilFinished: Long) {

            resend.text = (millisUntilFinished / 1000).toString() + "sec"
            println("Timer  : " + millisUntilFinished / 1000)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}