package com.marqueberry.memeberry

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.marqueberry.memeberry.cache.OfflineStorage.homeFeedBack

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i("TAGMain", "onCreate: ")

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)


        supportActionBar?.hide()

        window.statusBarColor= ContextCompat.getColor(this,R.color.yellowNoti)
        setContentView(R.layout.activity_main)


    }


}