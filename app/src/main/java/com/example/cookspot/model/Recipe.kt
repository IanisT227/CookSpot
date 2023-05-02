package com.example.cookspot.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val name: String = "",
    val duration: String = "",
    val imageUri: Uri,
    val makes: String = "",
    val difficulty: String = "",
    val tags: ArrayList<String> = ArrayList<String>(),
    val ingredients: String = "",
    val cookingProcess: String = "",
    val publisherId: String = ""
):Parcelable