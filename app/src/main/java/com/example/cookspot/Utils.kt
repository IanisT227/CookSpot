package com.example.cookspot

import android.app.Activity
import android.util.Patterns
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.storage.FirebaseStorage
import com.tapadoo.alerter.Alerter


fun logTag(tag: String, message: String = "") {
    println("[$tag] $message")
}

fun checkMail(mailEditText: TextInputEditText): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(mailEditText.text.toString())
        .matches() && mailEditText.text?.length!! > 7
}

fun checkUserOrPassword(userOrPasswordEditText: TextInputEditText): Boolean {
    return if (!userOrPasswordEditText.text.isNullOrEmpty()) {
        userOrPasswordEditText.text!!.length > 8
    } else
        false
}

fun checkName(nameEditText: TextInputEditText): Boolean {
    val regex = Regex("^[a-zA-Z ]+\$")
    return regex.matches(nameEditText.text.toString())
}

fun showError(bodyText: String, activity: Activity) = Alerter.create(activity)
    .setTitle("Error")
    .setText(bodyText)
    .setBackgroundColorRes(R.color.primary_orange)
    .setDuration(ERROR_DURATION)
    .show()

fun showRecipeAlerter(bodyText: String, activity: Activity) = Alerter.create(activity)
    .setTitle("Recipe added")
    .setText(bodyText)
    .setBackgroundColorRes(R.color.primary_orange)
    .setDuration(ERROR_DURATION)
    .show()

fun showFollowerAlerter(activity: Activity) = Alerter.create(activity)
    .setTitle("User followed")
    .setBackgroundColorRes(R.color.primary_orange)
    .setDuration(ERROR_DURATION)
    .show()

fun ImageView.loadUrl(url: String?) {
    val firebaseStorage = FirebaseStorage.getInstance().getReference("recipes_pictures/$url")

    firebaseStorage.downloadUrl.addOnSuccessListener { it ->

        Glide.with(this)
            .load(it)
            .placeholder(R.drawable.ic_launcher_foreground)
            .error(R.drawable.ic_launcher_background)
            .into(this)
    }.addOnFailureListener {
        // Handle any errors
    }
}

const val ERROR_DURATION = 2500L
const val DATABASE_URL = "https://cookspot-a1a8c-default-rtdb.europe-west1.firebasedatabase.app/"