package com.example.cookspot.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookspot.logTag
import com.example.cookspot.model.Recipe
import com.example.cookspot.model.RecipeStatus
import com.example.cookspot.repository.UserDataInternalStorageManager
import com.example.cookspot.service.RecipeService
import kotlinx.coroutines.launch

class RecipeViewModel(
    private val recipeService: RecipeService,
    private val internalStorageManager: UserDataInternalStorageManager
) : ViewModel() {

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isPosted: MutableLiveData<Boolean> = MutableLiveData()
    val isPosted: LiveData<Boolean>
        get() = _isPosted

    private val _isError: MutableLiveData<String?> = MutableLiveData()
    val isError: LiveData<String?>
        get() = _isError

    private val _recipeTags: MutableLiveData<HashMap<String, Boolean>?> = MutableLiveData()
    val recipeTags: LiveData<HashMap<String, Boolean>?>
        get() = _recipeTags

    private val _postedRecipesList: MutableLiveData<MutableList<Pair<Recipe, RecipeStatus>>?> =
        MutableLiveData()
    val postedRecipesList: LiveData<MutableList<Pair<Recipe, RecipeStatus>>?>
        get() = _postedRecipesList

    private val _recommendedRecipesList: MutableLiveData<MutableList<Recipe?>> = MutableLiveData()
    val recommendedRecipesList: LiveData<MutableList<Recipe?>>
        get() = _recommendedRecipesList

    private val _cookedRecipesList: MutableLiveData<MutableList<Recipe?>> = MutableLiveData()
    val cookedRecipesList: LiveData<MutableList<Recipe?>>
        get() = _cookedRecipesList

    private val _savedRecipesList: MutableLiveData<MutableList<Recipe?>> = MutableLiveData()
    val savedRecipeList: LiveData<MutableList<Recipe?>>
        get() = _savedRecipesList

    private val _cookedRecipesSize: MutableLiveData<Int> = MutableLiveData()
    val cookedRecipesSize: LiveData<Int>
        get() = _cookedRecipesSize

    private val _savedRecipesSize: MutableLiveData<Int> = MutableLiveData()
    val savedRecipesSize: LiveData<Int>
        get() = _savedRecipesSize

    private val _searchedRecipes: MutableLiveData<MutableList<Pair<Recipe, RecipeStatus>>?> =
        MutableLiveData()
    val searchedRecipes: LiveData<MutableList<Pair<Recipe, RecipeStatus>>?>
        get() = _searchedRecipes

    private val _isSaved: MutableLiveData<Boolean> = MutableLiveData()
    val isSaved: LiveData<Boolean>
        get() = _isSaved

    private val _isLiked: MutableLiveData<Boolean> = MutableLiveData()
    val isLiked: LiveData<Boolean>
        get() = _isLiked

    fun initFirebase() {
        viewModelScope.launch {
            recipeService.initFirebase()
            _isPosted.value = false
        }
    }

    fun getTags() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _recipeTags.value = recipeService.getTags().receive()
                Log.v(VIEWMODEL_TAG, _recipeTags.value.toString())
                _isError.value = recipeService.getIsErrorMessage()
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    fun getPostedRecipes(userId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val receivedRecipes = recipeService.getPostedRecipes(userId).receive()
                if (receivedRecipes != null) {
                    var recipeList = mutableListOf<Pair<Recipe, RecipeStatus>>()
                    for (recipe in receivedRecipes) {
                        recipeList.add(
                            Pair(
                                recipe.value,
                                RecipeStatus(
                                    recipeService.isRecipeInLiked(
                                        recipeId = recipe.value.imageUri,
                                        userId
                                    ),
                                    recipeService.isRecipeInSaved(
                                        recipeId = recipe.value.imageUri,
                                        userId
                                    )
                                )
                            )
                        )
                    }
                    _postedRecipesList.value =
                        recipeList.sortedByDescending { it.first.createdAt }.toMutableList()
                } else
                    _postedRecipesList.value = null
                _isError.value = recipeService.getIsErrorMessage()
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    fun uploadRecipe(recipeToUpload: Recipe) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                recipeService.uploadRecipe(recipeToUpload)
                _isError.value = recipeService.getIsErrorMessage()
                _isPosted.value = true
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    fun getCookedRecipeList() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _cookedRecipesList.value =
                    recipeService.getCookedRecipesList(internalStorageManager.getUserId() !!)
                logTag("cookedList", _cookedRecipesList.value.toString())
                _isError.value = recipeService.getIsErrorMessage()
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    fun addToCooked(recipeId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                recipeService.addPostToCooked(internalStorageManager.getUserId() !!, recipeId)
                _isError.value = recipeService.getIsErrorMessage()
                _isPosted.value = true
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    fun removeFromCooked(recipeId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                recipeService.removePostFromCooked(internalStorageManager.getUserId() !!, recipeId)
                _isError.value = recipeService.getIsErrorMessage()
                _isPosted.value = true
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    fun getRecipesListSize() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _cookedRecipesSize.value =
                    recipeService.getCookedRecipesSize(internalStorageManager.getUserId() !!)
                _savedRecipesSize.value =
                    recipeService.getSavedRecipesSize(internalStorageManager.getUserId() !!)
                _isError.value = recipeService.getIsErrorMessage()
                _isPosted.value = true
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    fun getSavedRecipeList() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _savedRecipesList.value =
                    recipeService.getSavedRecipesList(internalStorageManager.getUserId() !!)
                logTag("savedList", _savedRecipesList.value.toString())
                _isError.value = recipeService.getIsErrorMessage()
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    fun addToSaved(recipeId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                recipeService.addPostToSaved(internalStorageManager.getUserId() !!, recipeId)
                _isError.value = recipeService.getIsErrorMessage()
                _isPosted.value = true
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    fun removeFromSaved(recipeId: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                recipeService.removePostFromSaved(internalStorageManager.getUserId() !!, recipeId)
                _isError.value = recipeService.getIsErrorMessage()
                _isPosted.value = true
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    fun handleSave(recipeId: String) {
        viewModelScope.launch {
            isRecipeSaved(recipeId)
            if (_isSaved.value == true) {
                removeFromSaved(recipeId)
            } else {
                addToSaved(recipeId)
            }
        }
    }

    fun isRecipeSaved(recipeId: String) {
        viewModelScope.launch {
            _isSaved.value = recipeService.isRecipeInSaved(recipeId, getUserId() !!)
        }
    }

    fun likeRecipe(recipeId: String) {
        viewModelScope.launch {
            try {
                recipeService.likeRecipe(recipeId, getUserId() !!)
                _isError.value = recipeService.getIsErrorMessage()
                _isPosted.value = true
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isError.value = null
            }
        }
    }

    fun unlikeRecipe(recipeId: String) {
        viewModelScope.launch {
            try {
                recipeService.unlikeRecipe(recipeId, getUserId() !!)
                _isError.value = recipeService.getIsErrorMessage()
                _isPosted.value = true
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isError.value = null
            }
        }
    }

    fun handleLike(recipeId: String) {
        viewModelScope.launch {
            isRecipeLiked(recipeId)
            if (isLiked.value == true) {
                unlikeRecipe(recipeId)
            } else {
                likeRecipe(recipeId)
            }
        }
    }

    fun isRecipeLiked(recipeId: String) {
        viewModelScope.launch {
            _isLiked.value = recipeService.isRecipeInLiked(recipeId, getUserId() !!)
        }
    }

    fun searchRecipes(searchTerm: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val recipeList = recipeService.searchRecipes(searchTerm)
                if (recipeList != null) {
                    val recipeListStatus = mutableListOf<Pair<Recipe, RecipeStatus>>()
                    for (recipe in recipeList) {
                        recipeListStatus.add(
                            Pair(
                                recipe !!,
                                RecipeStatus(
                                    recipeService.isRecipeInLiked(
                                        recipeId = recipe.imageUri,
                                        internalStorageManager.getUserId() !!
                                    ),
                                    recipeService.isRecipeInSaved(
                                        recipeId = recipe.imageUri,
                                        internalStorageManager.getUserId() !!
                                    )
                                )
                            )
                        )
                    }
                    _searchedRecipes.value = recipeListStatus
                    logTag("searchResults", _searchedRecipes.value.toString())
                }
                _isError.value = recipeService.getIsErrorMessage()
                _isPosted.value = true
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    fun getRecommendedRecipeList(){
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _recommendedRecipesList.value =
                    recipeService.getRecommendedRecipes(internalStorageManager.getUserId() !!)
                logTag("recommendedList", _recommendedRecipesList.value.toString())
                _isError.value = recipeService.getIsErrorMessage()
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    private fun getUserId() = internalStorageManager.getUserId()

    companion object {
        const val VIEWMODEL_TAG = "RECIPE_VIEWMODEL"
    }
}