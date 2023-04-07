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

    companion object {
        const val KEY_PREFERENCES = "com.example.cookspot.KEY_PREFERENCES"
        const val KEY_IS_LOGGED_IN = "com.example.cookspot.KEY_IS_LOGGED_IN"
    }
}