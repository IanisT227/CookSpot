package com.example.cookspot.model

data class RecipeStatus(
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isCooked: Boolean = false
)
