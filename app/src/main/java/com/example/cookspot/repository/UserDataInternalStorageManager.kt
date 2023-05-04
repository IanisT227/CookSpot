package com.example.cookspot.repository

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

class UserDataInternalStorageManager(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(KEY_PREFERENCES, MODE_PRIVATE)

    fun getIsUserLoggedIn(): Boolean = preferences.getBoolean(KEY_IS_LOGGED_IN, false)

    fun setIsUserLoggedIn(isLoggedIn: Boolean) =
        preferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()

    fun getUserFullName(): String? = preferences.getString(KEY_USER_FULL_NAME, "")

    fun setUserFullName(userFullName: String) = preferences.edit().putString(
        KEY_USER_FULL_NAME, userFullName
    ).apply()

    fun getUserUsername(): String? = preferences.getString(KEY_USER_USERNAME, "")

    fun setUserUsername(userUsername: String) = preferences.edit().putString(
        KEY_USER_USERNAME, userUsername
    ).apply()

    companion object {
        const val KEY_PREFERENCES = "com.example.cookspot.KEY_PREFERENCES"
        const val KEY_IS_LOGGED_IN = "com.example.cookspot.KEY_IS_LOGGED_IN"
        const val KEY_USER_FULL_NAME = "com.example.cookspot.KEY_USER_FULL_NAME"
        const val KEY_USER_USERNAME = "com.example.cookspot.KEY_USER_USERNAME"

    }
}