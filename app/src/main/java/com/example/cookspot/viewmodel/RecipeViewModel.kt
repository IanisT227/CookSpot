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

    private val _postedRecipesList: MutableLiveData<List<Recipe>?> = MutableLiveData()
    val postedRecipesList: LiveData<List<Recipe>?>
        get() = _postedRecipesList

    private val _cookedRecipesList: MutableLiveData<MutableList<Recipe>?> = MutableLiveData()
    val cookedRecipesList: LiveData<MutableList<Recipe>?>
        get() = _cookedRecipesList

    private val _savedRecipesList: MutableLiveData<List<Recipe>?> = MutableLiveData()
    val savedRecipeList: LiveData<List<Recipe>?>
        get() = _savedRecipesList

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
                        receivedRecipes.values.toMutableList().sortedByDescending { it.createdAt }
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

    private fun getRecipeById(recipeId: String): Recipe? {
        var receivedRecipe: Recipe? = null
        viewModelScope.launch {
            try {
                receivedRecipe = recipeService.getRecipeById(recipeId).receive()
                _isError.value = recipeService.getIsErrorMessage()
            } catch (e: Exception) {
                internalStorageManager.setIsUserLoggedIn(true)
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
        return receivedRecipe
    }

    fun getCookedRecipeList() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val recipeList =
                    recipeService.getCookedRecipesIdList(internalStorageManager.getUserId() !!)
                        .receive()
                for (recipeId in recipeList !!.keys) {
                    val newRecipe = getRecipeById(recipeId)
                    logTag("newRecipe", newRecipe.toString())
                    if (newRecipe != null) {
                        _cookedRecipesList.value !!.add(newRecipe)
                    }
                }
                logTag("cookedList", _cookedRecipesList.value.toString())
                _isError.value = recipeService.getIsErrorMessage()
            } catch (e: Exception) {
                internalStorageManager.setIsUserLoggedIn(true)
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