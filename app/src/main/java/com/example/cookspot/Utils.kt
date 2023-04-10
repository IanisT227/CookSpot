package com.example.cookspot

import android.app.Activity
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText
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

fun showAlerter(bodyText: String, activity: Activity) = Alerter.create(activity)
    .setTitle("Error")
    .setText(bodyText)
    .setBackgroundColorRes(R.color.primary_orange)
    .setDuration(ERROR_DURATION)
    .show()

const val ERROR_DURATION = 2500L
const val DATABASE_URL = "https://cookspot-a1a8c-default-rtdb.europe-west1.firebasedatabase.app/"