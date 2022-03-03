package com.marqueberry.memeberry

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.marqueberry.memeberry.cache.OfflineStorage
import java.util.*
import kotlin.properties.Delegates

class SplashFragment : Fragment() {


    companion object{
        private const val MY_PREFS_NAME = "User"
        private const val logged = "loggedIn"
    }

    private var value by Delegates.notNull<Int>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i("TAGSplash", "onViewCreated: ")

//        if(OfflineStorage.getProfileData(requireContext())){
////            findNavController().navigate(R.id.action_splashFragment_to_home_nav)
//            findNavController().navigate(R.id.action_splashFragment_to_homeActivity)
//        }else{
//            findNavController().navigate(R.id.action_splashFragment_to_authFragment)
//
//        }



        Handler(Looper.getMainLooper()).postDelayed({
            if(OfflineStorage.getProfileData(requireContext())){
//            findNavController().navigate(R.id.action_splashFragment_to_home_nav)
                findNavController().navigate(R.id.action_splashFragment_to_homeActivity)
            } else{
                findNavController().navigate(R.id.action_splashFragment_to_authFragment)

            }
        },2000L)


    }

//    override fun onResume() {
//        super.onResume()
//        Handler(Looper.getMainLooper()).postDelayed({
//            if(OfflineStorage.getProfileData(requireContext())){
////            findNavController().navigate(R.id.action_splashFragment_to_home_nav)
//                findNavController().navigate(R.id.action_splashFragment_to_homeActivity)
//            } else{
//                findNavController().navigate(R.id.action_splashFragment_to_authFragment)
//            }
//        },2000L)
//    ------
//        Handler(Looper.getMainLooper()).postDelayed({
//            if(value == 0){
//                findNavController().navigate(R.id.home_nav)
//            }else{
//                lifecycleScope.launchWhenResumed {
//                    findNavController().navigate(R.id.action_splashFragment_to_authFragment)
//                }
//            }
//        },2000L)

//    }

}