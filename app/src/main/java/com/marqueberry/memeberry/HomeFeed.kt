package com.marqueberry.memeberry

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.marqueberry.memeberry.cache.OfflineStorage.homeFeedBack
import com.marqueberry.memeberry.cache.OfflineStorage.isUserLoggedIn


class HomeFeed : Fragment() {
lateinit var sharedpref : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeFeedBack=true
        isUserLoggedIn=true
        Log.i("TAGHomefeed", "onCreate: ")



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_feed, container, false)
    }

    override fun onStop() {
        super.onStop()
        homeFeedBack=false
    }

    override fun onResume() {
        super.onResume()
        homeFeedBack=true
    }



}