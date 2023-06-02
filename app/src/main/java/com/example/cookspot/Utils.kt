package com.example.cookspot

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.example.cookspot.model.Recipe
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

fun ImageView.loadRecipePhoto(url: String?) {
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

fun ImageView.loadProfilePhoto(url: String?) {
    val firebaseStorage = FirebaseStorage.getInstance().getReference("users_pictures/$url")

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

fun View.hideKeyboard(focusedView: View, activity: Activity) {
    val imm =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm?.hideSoftInputFromWindow(focusedView.windowToken, 0)
}

fun shareRecipe(recipe: Recipe, context: Context) {
    val text = "Check out this recipe I found on CookSpot:\n" +
            "Title: ${recipe.name}\n" +
            "Tags: ${recipe.tags[0]}, ${recipe.tags[1]}, ${recipe.tags[2]}\n" +
            "Ingredients: \n${recipe.ingredients}\n" +
            "Instructions: \n${recipe.cookingProcess}"

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    startActivity(context, shareIntent, null)
}

const val ERROR_DURATION = 2500L
const val DATABASE_URL = "https://cookspot-a1a8c-default-rtdb.europe-west1.firebasedatabase.app/"
const val LIKE_RECIPE = "like"
const val SAVE_RECIPE = "save"
const val VIEW_RECIPE = "view"
const val SHARE_RECIPE = "share"