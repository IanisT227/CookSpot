package com.example.cookspot.model

import java.text.SimpleDateFormat
import java.util.*

data class User(
    val username: String = "",
    val fullName: String = "",
    val email: String = "",
    val imageUri: String = "",
    val createdAt: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(
        Calendar.getInstance().time
    ),
    val publishedRecipes: HashMap<String, Boolean> = HashMap(),
    val followedUsers: HashMap<String, Boolean> = HashMap(),
    val cookedRecipes: HashMap<String, Boolean> = HashMap(),
    val savedRecipes: HashMap<String, Boolean> = HashMap()
)