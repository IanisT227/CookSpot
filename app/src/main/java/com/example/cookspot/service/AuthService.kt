package com.example.cookspot.service

import com.example.cookspot.logTag
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthService {
    private lateinit var firebaseAuth: FirebaseAuth
    private var isErrorMessage: String? = null

    fun initFirebase() {
        firebaseAuth = Firebase.auth
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

    suspend fun registerUser(email: String, password: String): Boolean {
        try {
            isErrorMessage = null
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
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