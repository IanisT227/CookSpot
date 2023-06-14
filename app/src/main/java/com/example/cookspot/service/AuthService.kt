package com.example.cookspot.service

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.example.cookspot.DATABASE_URL
import com.example.cookspot.logTag
import com.example.cookspot.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.tasks.await
import java.io.File

class AuthService {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseReference: DatabaseReference
    private lateinit var firebaseStorage: FirebaseStorage
    private var isErrorMessage: String? = null

    fun initFirebase() {
        firebaseAuth = Firebase.auth
        firebaseDatabase = Firebase.database(DATABASE_URL)
        firebaseReference = firebaseDatabase.getReference("users")
        firebaseStorage = FirebaseStorage.getInstance()
    }

    suspend fun uploadPicture(imageUri: Uri, userId: String) {
        val storageReference = firebaseStorage.getReference("users_pictures/" + userId)
        try {
            storageReference.putFile(imageUri).await()
        } catch (e: Exception) {
            isErrorMessage = e.message
        }
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
            return firebaseAuth.currentUser!!.uid
        } catch (e: Exception) {
            isErrorMessage = e.message
        }
        return null
    }

    fun getCurrentUser(): Channel<User?> {
        val channel = Channel<User?>()
        firebaseReference.child(firebaseAuth.currentUser!!.uid)
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val value = snapshot.getValue<User>()
                    channel.trySend(value).isSuccess
                    channel.close()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("userData", "Failed to read value.", error.toException())
                    channel.trySend(null).isSuccess
                    channel.close()
                }

            })
        return channel
    }

    suspend fun getUserById(userId: String): User? {
        var user: User? = null
        firebaseReference.child(userId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    user = task.result.getValue(User::class.java)

                } else {
                    isErrorMessage = task.exception?.message
                }
            }.await()
        return user
    }

    suspend fun updateUser(username: String, fullName: String) {
        firebaseReference.child(firebaseAuth.currentUser!!.uid).child("username")
            .setValue(username).await()
        firebaseReference.child(firebaseAuth.currentUser!!.uid).child("fullName")
            .setValue(fullName).await()
    }

    suspend fun followUser(userId: String) {
        firebaseReference.child(firebaseAuth.currentUser!!.uid).child("followedUsers")
            .child(userId).setValue(true).await()
    }

    fun getIsErrorMessage() = isErrorMessage
    suspend fun resetPassword(email: String): Boolean {
        var result: Boolean = false
        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                result = true
            } else {
                isErrorMessage = task.exception?.message
            }
        }.await()
        return result
    }

    companion object {
        const val SERVICE_TAG = "AuthService"
    }
}