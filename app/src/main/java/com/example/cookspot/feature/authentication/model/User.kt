package com.example.cookspot.feature.authentication.model

data class User(
    val username: String = "",
    val fullName: String = "",
    val email: String = "",
) {
    constructor() : this("", "", "")
}
