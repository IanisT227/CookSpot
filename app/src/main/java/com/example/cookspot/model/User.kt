package com.example.cookspot.model

data class User(
    val username: String = "",
    val fullName: String = "",
    val email: String = "",
    val publishedRecipes: ArrayList<String> = ArrayList(),
    val followedUsers: ArrayList<String>,
)