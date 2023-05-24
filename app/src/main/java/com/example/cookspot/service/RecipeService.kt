package com.example.cookspot.service

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.example.cookspot.DATABASE_URL
import com.example.cookspot.logTag
import com.example.cookspot.model.Recipe
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
import java.util.*

class RecipeService {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseRecipeReference: DatabaseReference
    private lateinit var firebaseUserReference: DatabaseReference
    private lateinit var firebaseReference: DatabaseReference
    private lateinit var firebaseStorage: FirebaseStorage
    private var isErrorMessage: String? = null

    fun initFirebase() {
        firebaseAuth = Firebase.auth
        firebaseDatabase = Firebase.database(DATABASE_URL)
        firebaseRecipeReference = firebaseDatabase.getReference("recipes")
        firebaseReference = firebaseDatabase.reference
        firebaseUserReference = firebaseDatabase.getReference("users")
        firebaseStorage = FirebaseStorage.getInstance()
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

    private suspend fun uploadPicture(imageUri: Uri, recipeId: String) {
        val storageReference = firebaseStorage.getReference("recipes_pictures/" + recipeId)
        try {
            storageReference.putFile(imageUri).await()
        } catch (e: Exception) {
            isErrorMessage = e.message
        }
    }

    suspend fun uploadRecipe(recipe: Recipe) {
        try {
            val recipeId: String = UUID.randomUUID().toString()
            firebaseRecipeReference.child(recipeId)
                .setValue(recipe.copy(imageUri = recipeId))
            uploadPicture(recipe.imageUri.toUri(), recipeId)
            addRecipeToUser(recipe.publisherId, recipeId)
        } catch (e: Exception) {
            isErrorMessage = e.message
        }
    }

    private suspend fun addRecipeToUser(userId: String, recipeId: String) {
        try {
            firebaseUserReference.child(userId).child("publishedRecipes").child(recipeId)
                .setValue(true).await()
        } catch (e: Exception) {
            isErrorMessage = e.message
        }
    }

    fun getPostedRecipes(userId: String): Channel<HashMap<String, Recipe>?> {
        val channel = Channel<HashMap<String, Recipe>?>()
        firebaseDatabase.getReference("recipes").orderByChild("publisherId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.getValue<HashMap<String, Recipe>?>()
                    channel.trySend(value).isSuccess
                    channel.close()
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.w(SERVICE_TAG, "Failed to read value.", error.toException())
                    channel.trySend(null).isSuccess
                    channel.close()
                }
            })
        return channel
    }

    suspend fun addPostToCooked(userId: String, recipeId: String){
        try {
            firebaseUserReference.child(userId).child("cookedRecipes").child(recipeId)
                .setValue(true).await()
        } catch (e: Exception) {
            isErrorMessage = e.message
        }
    }

    suspend fun addPostToSaved(userId: String, recipeId: String) {
        try {
            firebaseUserReference.child(userId).child("savedRecipes").child(recipeId)
                .setValue(true).await()
        } catch (e: Exception) {
            isErrorMessage = e.message
        }
    }

    suspend fun removePostFromSaved(userId: String, recipeId: String) {
        try {
            firebaseUserReference.child(userId).child("savedRecipes").child(recipeId)
                .removeValue().await()
        } catch (e: Exception) {
            isErrorMessage = e.message
        }
    }

    suspend fun getCookedRecipesList(userId: String): MutableList<Recipe?> {
        val receivedIdList: MutableList<String> = mutableListOf()
        val cookedRecipesList = mutableListOf<Recipe?>()
        firebaseUserReference.child(userId).child("cookedRecipes")
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    for (receivedPair in result.children) {
                        receivedIdList?.add(receivedPair.key.toString())
                    }
                } else {
                    isErrorMessage = task.exception?.message
                }
            }.await()

        for (recipeId in receivedIdList) {
            firebaseRecipeReference.child(recipeId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val result = task.result
                        result?.let {
                            cookedRecipesList.add(result.getValue(Recipe::class.java))
                        }
                    } else {
                        isErrorMessage = task.exception?.message
                    }
                }.await()
        }
        return cookedRecipesList
    }

    suspend fun getSavedRecipesList(userId: String): MutableList<Recipe?> {
        val receivedIdList: MutableList<String> = mutableListOf()
        val savedRecipesList = mutableListOf<Recipe?>()
        firebaseUserReference.child(userId).child("savedRecipes")
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    for (receivedPair in result.children) {
                        receivedIdList?.add(receivedPair.key.toString())
                    }
                } else {
                    isErrorMessage = task.exception?.message
                }
            }.await()

        for (recipeId in receivedIdList) {
            firebaseRecipeReference.child(recipeId).get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val result = task.result
                        result?.let {
                            savedRecipesList.add(result.getValue(Recipe::class.java))
                        }
                    } else {
                        isErrorMessage = task.exception?.message
                    }
                }.await()
        }
        return savedRecipesList
    }

    suspend fun getCookedRecipesSize(userId: String): Int {
        var size = 0L
        firebaseUserReference.child(userId).child("cookedRecipes").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    size = task.result.childrenCount

                } else {
                    isErrorMessage = task.exception?.message
                }
            }.await()
        return size.toInt()
    }

    suspend fun getSavedRecipesSize(userId: String): Int {
        var size = 0L
        firebaseUserReference.child(userId).child("savedRecipes").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    size = task.result.childrenCount

                } else {
                    isErrorMessage = task.exception?.message
                }
            }.await()
        return size.toInt()
    }

    suspend fun likeRecipe(recipeId: String) {
        val likes = getLikesForRecipe(recipeId) + 1
        logTag("likesnumber", likes.toString())
        firebaseRecipeReference.child(recipeId).child("likes").setValue(likes)
    }

    suspend fun unlikeRecipe(recipeId: String) {
        val likes = getLikesForRecipe(recipeId) - 1
        logTag("likesnumber", likes.toString())
        firebaseRecipeReference.child(recipeId).child("likes").setValue(likes)
    }

    suspend fun getLikesForRecipe(recipeId: String): Long {
        var likesNumber: Long = 0
        firebaseRecipeReference.child(recipeId).child("likes").get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val result = task.result
                likesNumber = result.getValue(Long::class.java)!!
            } else {
                isErrorMessage = task.exception?.message
            }
        }.await()
        return likesNumber
    }

    fun getIsErrorMessage() = isErrorMessage

    companion object {
        const val SERVICE_TAG = "RecipeService"
    }
}