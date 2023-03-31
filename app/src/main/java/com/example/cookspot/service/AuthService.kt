package com.example.cookspot.service

import com.example.cookspot.logTag
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthService {
    private lateinit var firebaseAuth: FirebaseAuth

    suspend fun initFirebase() {
        firebaseAuth = Firebase.auth
    }

    suspend fun loginUser(email: String, password: String): String? {
        var errorMessage: String? = null
        try {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    errorMessage = task.exception!!.message
                    if (errorMessage.isNullOrEmpty())
                    {
                        errorMessage = "Invalid credentials"
                    }
                }
            }
        } catch (e: Exception) {
            errorMessage = e.message
        }

        logTag("isErrorValueService", errorMessage.toString())
        return errorMessage
    }
}