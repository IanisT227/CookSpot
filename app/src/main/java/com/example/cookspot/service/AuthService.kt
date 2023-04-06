package com.example.cookspot.service

import com.example.cookspot.logTag
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class AuthService {
    private lateinit var firebaseAuth: FirebaseAuth
    private var isErrorMessage: String? = null

    suspend fun initFirebase() {
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

    fun getCurrentUser(): String? {
        try {
            isErrorMessage = null
            val firebaseDb = Firebase.database
            val username = firebaseDb.getReference(firebaseAuth.currentUser!!.uid).child("username").get()
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