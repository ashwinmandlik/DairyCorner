package com.marqueberry.memeberrieticket.cache

import android.content.Context

object OfflineStorage {

    var homeFeedBack=false

    var isUserLoggedIn=false



    fun getProfileData(c: Context): Boolean{
        val storagePref= c.getSharedPreferences("isProfileCreated",0)
        return storagePref.getBoolean("isProfileCreated",false)
    }
    fun setProfileData(c: Context, boolean:Boolean){
        val storagePref= c.getSharedPreferences("isProfileCreated",0)
        storagePref.edit().putBoolean("isProfileCreated",boolean).apply()
    }

}