package com.marqueberry.memeberrieticket.profile

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.storage.FirebaseStorage
import com.marqueberry.memeberrieticket.R
import com.marqueberry.memeberrieticket.UserProfileData
import com.marqueberry.memeberrieticket.cache.OfflineStorage.setProfileData
import com.marqueberry.memeberrieticket.databinding.FragmentProfileBinding
import java.util.*

class ProfileFragment : Fragment() {

    companion object {
        private const val TAG = "MainActivity"
    }

    var valid = true
    private lateinit var progressDialog: ProgressDialog
    private var mDateSetListener: DatePickerDialog.OnDateSetListener? = null
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var list: ArrayList<UserProfileData>
    private var userNameData: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("PleaseWait")
        progressDialog.setCanceledOnTouchOutside(false)
        list = arrayListOf<UserProfileData>()

        //Pick images
        binding.profileImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 82)

        }

        binding.saveProfile.setOnClickListener {

            if (selectedProfileUri != null) {
                uploadImageToFirebaseStorage()
            } else {
                saveUserProfileData()
            }
        }


    }

    private fun uploadImageToFirebaseStorage() {

        val fullName = binding.name.editableText.toString()
        val username = binding.userName.editableText.toString()
        val code = binding.name2.editableText.toString()
        checkField(binding.name)
        checkField(binding.userName)


        if (username.length < 4) {
            progressDialog.dismiss()
            binding.userName.error = "UserName should be at least 4 character"
            binding.userName.requestFocus()
            return
        }
        if (username.length > 32) {
            progressDialog.dismiss()
            binding.userName.error = "UserName should be less than 32 character"
            binding.userName.requestFocus()
            return
        }

        if (valid) {
            progressDialog.show()
            val filename = UUID.randomUUID().toString()
            val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
            ref.putFile(selectedProfileUri!!)
                .addOnSuccessListener {
//                    Toast.makeText(
//                        context,
//                        "Upload Success",
//                        Toast.LENGTH_SHORT
//                    ).show()
                    //Get Download Url
                    ref.downloadUrl.addOnSuccessListener {
                        //Save Data into Firebase Database
                        saveUserProfileData(it.toString())
                    }
                }
        } else {
            progressDialog.dismiss()
            Toast.makeText(context, R.string.dataValid, Toast.LENGTH_LONG).show()
        }
    }

    private fun saveUserProfileData() {
        val fullName = binding.name.editableText.toString()
        val username = binding.userName.editableText.toString()
        val code = binding.name2.editableText.toString()
        val radio: RadioButton? = view?.findViewById(binding.gender.checkedRadioButtonId)
        val gender = "${radio?.text}"
        if (username.length < 4) {
            progressDialog.dismiss()
            binding.userName.error = "UserName should be at least 4 character"
            binding.userName.requestFocus()
            return
        }
        if (TextUtils.isEmpty(code)) {
            progressDialog.dismiss()
            binding.name2.error = "Enter Code"
            binding.name2.requestFocus()
            return
        }
        Log.i(TAG, "saveUserProfileData: " + fullName + "  username= " + username)

        FirebaseFirestore.getInstance().collection("Users").whereEqualTo("userName", username).get()
            .addOnSuccessListener {
//                for (documentSnapshort: QueryDocumentSnapshot in it) {
                Log.i("ashwinscheck", "saveUserProfileData: x ")

                if (!it.documents.isEmpty()) {
                    Log.d("data", "Username Exists")
                    userNameData = true
                } else {
                    Log.d("data", "Username Not Exists")
                    userNameData = false
                }

                if (!userNameData) {

                    val users = FirebaseAuth.getInstance().currentUser as FirebaseUser
                    val user = UserProfileData(
                        fullName = fullName,
                        userName = username,
                        phoneNumber = users.phoneNumber!!,
                        code = code,
                        gender = gender,
                        null
                    )

                    FirebaseFirestore.getInstance().collection("Users")
                        .document(users.phoneNumber!!).set(user).addOnCompleteListener {
                            Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show()
                            if (progressDialog.isShowing) progressDialog.dismiss()
                            setProfile(true)

                            //Navigation.findNavController(requireView()).navigate(R.id.home_nav)
                            findNavController().navigate(R.id.action_profileFragment_to_homeActivity)

                        }.addOnFailureListener {
                            Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                            if (progressDialog.isShowing) progressDialog.dismiss()
                        }
                } else {
                    progressDialog.dismiss()
                    binding.userName.error = "User Name Already Exists"
                    binding.userName.requestFocus()

                }
                userNameData = false

//                }
            }


//        ref.child(user.PhoneNumber).setValue(user)
//            .addOnSuccessListener {
//                progressDialog.dismiss()
//                Toast.makeText(context, "Upload Data Successfully", Toast.LENGTH_LONG).show()
////                val action =
////                    UpdateProfileFragmentDirections.actionUpdateProfileFragmentToUserProfileFragment(
////                        user.PhoneNumber
////                    )
////                findNavController().navigate(action)
//
//            }

    }

    private fun setProfile(b: Boolean) {
        setProfileData(requireContext(), b)

    }

    private fun fetchProfiles(username: String) {
        FirebaseFirestore.getInstance().collection("Users").whereEqualTo("userName", username).get()
            .addOnSuccessListener {
                for (documentSnapshort: QueryDocumentSnapshot in it) {
                    if (documentSnapshort.exists()) {
                        Log.d("data", "Username Exists")
                        userNameData = true
                    } else {
                        Log.d("data", "Username Not Exists")
                        userNameData = false
                    }
                }
            }
    }

    private fun saveUserProfileData(profileImageUrl: String) {

        val fullName = binding.name.editableText.toString()
        val username = binding.userName.text.toString()
        val code = binding.name2.editableText.toString()
        val radio: RadioButton? = view?.findViewById(binding.gender.checkedRadioButtonId)
        val gender = "${radio?.text}"

        if (username.length < 4) {
            progressDialog.dismiss()
            binding.userName.error = "UserName should be at least 4 character"
            binding.userName.requestFocus()
            return
        }
        if (TextUtils.isEmpty(code)) {
            progressDialog.dismiss()
            binding.name2.error = "Enter Code"
            binding.name2.requestFocus()
            return
        }
        //     fetchProfiles(username)


        FirebaseFirestore.getInstance().collection("Users").whereEqualTo("userName", username).get()
            .addOnSuccessListener {
                //for (documentSnapshort: QueryDocumentSnapshot in it) {
                if (!it.documents.isEmpty()) {
                    Log.d("data", "Username Exists")
                    userNameData = true
                } else {
                    Log.d("data", "Username Not Exists")
                    userNameData = false
                }

//                        ...
//                        .addOnSuccessListener {
////                for (documentSnapshort: QueryDocumentSnapshot in it) {
//                            Log.i("ashwinscheck", "saveUserProfileData: x ")
//
//                            if (!it.documents.isEmpty()) {
//                                Log.d("data", "Username Exists")
//                                userNameData = true
//                            } else {
//                                Log.d("data", "Username Not Exists")
//                                userNameData = false
//                            }
//                        ...


                if (!userNameData) {
                    val users = FirebaseAuth.getInstance().currentUser as FirebaseUser
                    val user = UserProfileData(
                        fullName = fullName,
                        userName = username,
                        phoneNumber = users.phoneNumber!!,
                        code = code,
                        gender = gender,
                        profileImageUrl = profileImageUrl
                    )


                    FirebaseFirestore.getInstance().collection("Users")
                        .document(users.phoneNumber!!).set(user).addOnCompleteListener {
                            Toast.makeText(context, "Successful", Toast.LENGTH_SHORT).show()
                            if (progressDialog.isShowing) progressDialog.dismiss()
                            //Navigation.findNavController(requireView()).navigate(R.id.home_nav)
                            setProfile(true)

                            findNavController().navigate(R.id.action_profileFragment_to_homeActivity)
                        }.addOnFailureListener {
                            Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
                            if (progressDialog.isShowing) progressDialog.dismiss()
                        }
                } else {
                    progressDialog.dismiss()
                    binding.userName.error = "User Name Already Exists"
                    binding.userName.requestFocus()

                }
                userNameData = false

            }
    }


//        val ref =
//            FirebaseDatabase.getInstance().getReference("userProfileData")
//
//
//
//        if (valid) {
//            progressDialog.show()
//            val user = UserProfileData(
//                fullName,
//                username,
//                users.phoneNumber!!,
//                code,
//                null
//            )
//
//            ref.child(user.PhoneNumber).setValue(user)
//                .addOnSuccessListener {
//                    progressDialog.dismiss()
//                    Toast.makeText(context, "Data Upload SuccessFully", Toast.LENGTH_LONG).show()
////                    val action =
////                        UpdateProfileFragmentDirections.actionUpdateProfileFragmentToUserProfileFragment(
////                            user.PhoneNumber
////                        )
////                    findNavController().navigate(action)
//          //          Navigation.findNavController(requireView()).navigate(R.id.authFragment)
//
//                }
//        } else {
//            progressDialog.dismiss()
//            Toast.makeText(context, R.string.dataValid, Toast.LENGTH_LONG).show()
//        }





var selectedProfileUri: Uri? = null

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    if (requestCode == 82 && resultCode == AppCompatActivity.RESULT_OK && data != null) {
        selectedProfileUri = data.data
        binding.profileImage.setImageURI(selectedProfileUri)
    }
}


private fun checkField(textField: EditText): Boolean {
    if (textField.text.toString().isEmpty()) {
        textField.error = "Error"
        textField.requestFocus()
        valid = false
    } else {
        valid = true
    }
    return valid
}

override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
}




}