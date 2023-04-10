package com.example.cookspot.feature.authentication.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val fullName: String,
    val email: String,
)
