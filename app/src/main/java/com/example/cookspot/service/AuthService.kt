package com.example.cookspot.service

import android.util.Log
import com.example.cookspot.DATABASE_URL
import com.example.cookspot.feature.authentication.model.User
import com.example.cookspot.logTag
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthService {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseReference: DatabaseReference
    private var isErrorMessage: String? = null

    fun initFirebase() {
        firebaseAuth = Firebase.auth
        firebaseDatabase = Firebase.database(DATABASE_URL)
        firebaseReference = firebaseDatabase.getReference("users")
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        try {
            isErrorMessage = null
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            return true
        } catch (e: Exception) {
            isErrorMessage = e.message
        }
        logTag(SERVICE_TAG, isErrorMessage.toString())
        return false
    }

    suspend fun registerUser(
        email: String,
        password: String,
        username: String,
        fullName: String
    ): Boolean {
        try {
            isErrorMessage = null
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            firebaseReference.child(firebaseAuth.currentUser!!.uid)
                .setValue(User(email = email, username = username, fullName = fullName))
            return true
        } catch (e: Exception) {
            isErrorMessage = e.message
        }
        logTag(SERVICE_TAG, isErrorMessage.toString())
        return false
    }

    suspend fun logOutUser() {
        try {
            isErrorMessage = null
            firebaseAuth.signOut()
        } catch (e: Exception) {
            isErrorMessage = e.message
        }
        logTag(SERVICE_TAG, isErrorMessage.toString())
    }

    fun getCurrentUserId(): String? {
        try {
            isErrorMessage = null
            initFirebase()
            val userId = firebaseAuth.currentUser!!.uid
            Log.v("UserId", userId)
            return firebaseAuth.currentUser!!.uid
        } catch (e: Exception) {
            isErrorMessage = e.message
        }
        return null
    }

    fun getIsErrorMessage() = isErrorMessage

    companion object {
        const val SERVICE_TAG = "AuthService"
    }
}