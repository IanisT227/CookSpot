package com.example.cookspot.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Recipe(
    val name: String = "",
    val duration: String = "",
    val imageUri: String = "",
    val makes: String = "",
    val difficulty: String = "",
    val tags: ArrayList<String> = ArrayList(),
    val ingredients: String = "",
    val cookingProcess: String = "",
    val publisherId: String = "",
    val publisherName: String = "",
    val createdAt: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(
        Calendar.getInstance().time
    ),
    val likes: Int = 0
):Parcelable