package com.example.cookspot.service

import android.util.Log
import com.example.cookspot.DATABASE_URL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.Channel

class RecipeService {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseReference: DatabaseReference
    private lateinit var firebaseTagList: ArrayList<String>
    private var isErrorMessage: String? = null

    fun initFirebase() {
        firebaseAuth = Firebase.auth
        firebaseDatabase = Firebase.database(DATABASE_URL)
        firebaseReference = firebaseDatabase.getReference("recipes")
    }

    fun getTags(): Channel<HashMap<String, Boolean>?> {
        val channel = Channel<HashMap<String, Boolean>?>()
        firebaseDatabase.getReference("tags")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    val value = snapshot.getValue<HashMap<String, Boolean>?>()
                    channel.trySend(value).isSuccess
                    channel.close()
                    Log.v("tagsRecipe", "Value is: " + value)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(SERVICE_TAG, "Failed to read value.", error.toException())
                    channel.trySend(null).isSuccess
                    channel.close()
                }

            })
        return channel
    }

    fun getIsErrorMessage() = isErrorMessage

    companion object {
        const val SERVICE_TAG = "RecipeService"
    }
}