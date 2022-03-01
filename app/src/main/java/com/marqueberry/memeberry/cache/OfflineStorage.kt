package com.marqueberry.memeberry.cache

import android.content.Context

object OfflineStorage {
    fun getProfileData(c: Context): Boolean{
        val storagePref= c.getSharedPreferences("isProfileCreated",0)
        return storagePref.getBoolean("isProfileCreated",false)
    }
    fun setProfileData(c: Context, boolean:Boolean){
        val storagePref= c.getSharedPreferences("isProfileCreated",0)
        storagePref.edit().putBoolean("isProfileCreated",boolean).apply()
    }
}