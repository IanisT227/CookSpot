package com.example.cookspot.service

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.example.cookspot.DATABASE_URL
import com.example.cookspot.logTag
import com.example.cookspot.model.Recipe
import com.example.cookspot.model.RecipeStatus
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
import kotlin.collections.HashMap

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
        firebaseRecipeReference.orderByChild("createdAt")
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

    suspend fun addPostToCooked(userId: String, recipeId: String) {
        try {
            firebaseUserReference.child(userId).child("cookedRecipes").child(recipeId)
                .setValue(true).await()
            firebaseUserReference.child(userId).child("interactedRecipes").child(recipeId)
                .setValue(true).await()
        } catch (e: Exception) {
            isErrorMessage = e.message
        }
    }

    suspend fun removePostFromCooked(userId: String, recipeId: String) {
        try {
            firebaseUserReference.child(userId).child("cookedRecipes").child(recipeId)
                .removeValue().await()
        } catch (e: Exception) {
            isErrorMessage = e.message
        }
    }

    suspend fun addPostToSaved(userId: String, recipeId: String) {
        try {
            firebaseUserReference.child(userId).child("savedRecipes").child(recipeId)
                .setValue(true).await()
            firebaseUserReference.child(userId).child("interactedRecipes").child(recipeId)
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
                        receivedIdList.add(receivedPair.key.toString())
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
                        receivedIdList.add(receivedPair.key.toString())
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

    suspend fun isRecipeInSaved(recipeId: String, userId: String): Boolean {
        var isInSaved = false
        firebaseUserReference.child(userId).child("savedRecipes").child(recipeId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.exists()) {
                        logTag("existInLiked", "true")
                        isInSaved = true
                    } else {
                        logTag("existInLiked", "false")

                    }
                    logTag("recipecountt", task.result.childrenCount.toString())
                } else {
                    isErrorMessage = task.exception!!.message
                }
            }.await()
        return isInSaved
    }

    suspend fun likeRecipe(recipeId: String, userId: String) {
        val likes = getLikesForRecipe(recipeId) + 1
        logTag("likesnumber", likes.toString())
        firebaseRecipeReference.child(recipeId).child("likes").setValue(likes)
        firebaseUserReference.child(userId).child("likedRecipes").child(recipeId)
            .setValue(true).await()
        firebaseUserReference.child(userId).child("interactedRecipes").child(recipeId)
            .setValue(true).await()
    }

    suspend fun unlikeRecipe(recipeId: String, userId: String) {
        val likes = getLikesForRecipe(recipeId) - 1
        logTag("likesnumber", likes.toString())
        firebaseRecipeReference.child(recipeId).child("likes").setValue(likes)
        firebaseUserReference.child(userId).child("likedRecipes").child(recipeId)
            .removeValue().await()
    }

    suspend fun isRecipeInLiked(recipeId: String, userId: String): Boolean {
        var isInLiked = false
        firebaseUserReference.child(userId).child("likedRecipes").child(recipeId).get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (task.result.exists()) {
                        logTag("existInLiked", "true")
                        isInLiked = true
                    } else {
                        logTag("existInLiked", "false")

                    }
                    logTag("recipecountt", task.result.childrenCount.toString())
                } else {
                    isErrorMessage = task.exception!!.message
                }
            }.await()
        return isInLiked
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

    suspend fun searchRecipes(recipeName: String): MutableList<Recipe?> {
        val cookedRecipesList = mutableListOf<Recipe?>()
        firebaseRecipeReference.orderByChild("name").startAt(recipeName)
            .endAt("$recipeName\uf8ff")
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    for (receivedPair in result.children) {
                        logTag("receivedPair", receivedPair.toString())
                        cookedRecipesList.add(receivedPair.getValue(Recipe::class.java))
                    }
                } else {
                    isErrorMessage = task.exception?.message
                }
            }.await()
        return cookedRecipesList
    }

    suspend fun getRecommendedRecipes(userId: String): MutableList<Recipe?> {
        val receivedIdList: MutableList<String> = mutableListOf()
        val interactedTags: HashMap<String, Int> = hashMapOf()
        val recommendedRecipeList: MutableList<Recipe?> = mutableListOf()
        firebaseUserReference.child(userId).child("interactedRecipes").limitToFirst(10)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    for (receivedPair in result.children) {
                        receivedIdList.add(receivedPair.key.toString())
                    }
                } else {
                    isErrorMessage = task.exception?.message
                }
            }.await()

        for (recipeId in receivedIdList) {
            firebaseRecipeReference.child(recipeId).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    result?.let {
                        val receivedRecipe = result.getValue(Recipe::class.java)
                        for (tag in receivedRecipe!!.tags) {
                            if (interactedTags.get(tag) == null) {
                                interactedTags.put(tag, 1)
                            } else {
                                interactedTags.set(tag, interactedTags.get(tag)!! + 1)
                            }
                        }
                    }
                } else {
                    isErrorMessage = task.exception?.message
                }
            }.await()
        }

        val sortedInteractedTags =
            interactedTags.toList().sortedByDescending { (_, value) -> value }.take(4).toMap()
        logTag("taglISTT", sortedInteractedTags.toString())

        firebaseDatabase.getReference("recipes").orderByChild("likes").get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val result = task.result
                    result?.let {
                        for (receivedPair in result.children) {
                            val receivedRecipe = receivedPair.getValue(Recipe::class.java)
                            for (tag in receivedRecipe!!.tags)
                                if (sortedInteractedTags.containsKey(tag) && !recommendedRecipeList.contains(
                                        receivedRecipe
                                    )
                                ) {
                                    recommendedRecipeList.add(receivedRecipe)
                                }
                        }
                    }

                } else {
                    isErrorMessage = task.exception?.message
                }
            }.await()
        return recommendedRecipeList
    }

    fun getIsErrorMessage() = isErrorMessage

    companion object {
        const val SERVICE_TAG = "RecipeService"
    }
}