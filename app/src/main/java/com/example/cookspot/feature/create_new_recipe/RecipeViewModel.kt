package com.example.cookspot.feature.create_new_recipe

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookspot.service.RecipeService
import kotlinx.coroutines.launch

class RecipeViewModel(private val recipeService: RecipeService) : ViewModel() {

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isError: MutableLiveData<String?> = MutableLiveData()
    val isError: LiveData<String?>
        get() = _isError

    private val _recipeTags: MutableLiveData<HashMap<String, Boolean>?> = MutableLiveData()
    val recipeTags: LiveData<HashMap<String, Boolean>?>
        get() = _recipeTags

    fun initFirebase() {
        viewModelScope.launch {
            recipeService.initFirebase()
        }
    }

    fun getTags() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _recipeTags.value = recipeService.getTags().receive()
                Log.v("userdatavm", _recipeTags.value.toString())
                _isError.value = recipeService.getIsErrorMessage()
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }
}