package com.example.cookspot.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookspot.logTag
import com.example.cookspot.model.Recipe
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

    private val _postedRecipesList: MutableLiveData<MutableList<Recipe>?> = MutableLiveData()
    val postedRecipesList: LiveData<MutableList<Recipe>?>
        get() = _postedRecipesList

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
                    _postedRecipesList.value =
                        receivedRecipes.values.sortedByDescending { it.createdAt }.toMutableList()
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

    fun getUserId() = internalStorageManager.getUserId()

    companion object {
        const val VIEWMODEL_TAG = "RECIPE_VIEWMODEL"
    }
}