package com.marqueberry.memeberry



data class UserProfileData(
    val fullName: String,
    val UserName: String,
    val PhoneNumber: String,
    val code: String,
    val ProfileImageUrl: String? = null,
)