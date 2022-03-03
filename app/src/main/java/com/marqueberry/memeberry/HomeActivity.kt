package com.marqueberry.memeberry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.marqueberry.memeberry.cache.OfflineStorage
import com.marqueberry.memeberry.profile.ProfileFragment

class HomeActivity : AppCompatActivity() {
   private lateinit var homeFeedFragment:HomeFeed
   private lateinit var profileFragment:ProfileFragment
   private lateinit var campaignFragmennt:CampaignsFragment
   private lateinit var chatsFragment: ChatsFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.hide()

        setContentView(R.layout.activity_home)
        Log.i("TAGHomeActivity", "onCreate: ")


        val bottomNavigationView=findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val navController= findNavController(R.id.fragmentContainerView)
        bottomNavigationView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        if (!OfflineStorage.homeFeedBack){
            super.onBackPressed()
        }
    }
}