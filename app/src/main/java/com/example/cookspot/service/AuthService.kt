package com.example.cookspot.service

import android.util.Log
import com.example.cookspot.DATABASE_URL
import com.example.cookspot.logTag
import com.example.cookspot.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.Channel
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
                .setValue(
                    User(
                        email = email, username = username, fullName = fullName,
                        publishedRecipes = HashMap(), followedUsers = HashMap()
                    )
                )
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

    fun getCurrentUser(): Channel<User?> {
        val channel = Channel<User?>()
        var currentUser: User? = null
        firebaseReference.child(firebaseAuth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val value = snapshot.getValue<User>()
                    channel.trySend(value).isSuccess
                    channel.close()
                    Log.v("userData", "Value is: " + value)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("userData", "Failed to read value.", error.toException())
                    channel.trySend(null).isSuccess
                    channel.close()
                }

            })
        Log.v("userdataAS", currentUser.toString())
        return channel
    }

    fun getIsErrorMessage() = isErrorMessage

    companion object {
        const val SERVICE_TAG = "AuthService"
    }
}