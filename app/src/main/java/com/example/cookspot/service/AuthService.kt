package com.example.cookspot.service

import com.example.cookspot.logTag
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthService {
    private lateinit var firebaseAuth: FirebaseAuth

    suspend fun initFirebase() {
        firebaseAuth = Firebase.auth
    }

    suspend fun loginUser(email: String, password: String): String? {
        var errorMessage: String? = null
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            errorMessage = e.message
        }

        logTag(SERVICE_TAG, errorMessage.toString())
        return errorMessage
    }

    suspend fun registerUser(email: String, password: String): String? {
        var errorMessage: String? = null
        try {
            firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        } catch (e: Exception) {
            errorMessage = e.message
        }

        logTag(SERVICE_TAG, errorMessage.toString())
        return errorMessage
    }

    companion object{
        const val SERVICE_TAG = "AuthService"
    }
}