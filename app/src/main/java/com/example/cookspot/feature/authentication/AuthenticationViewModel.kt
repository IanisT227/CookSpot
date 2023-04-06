package com.example.cookspot.feature.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookspot.logTag
import com.example.cookspot.service.AuthService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class AuthenticationViewModel(val authService: AuthService) : ViewModel() {

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isError: MutableLiveData<String?> = MutableLiveData()
    val isError: LiveData<String?>
        get() = _isError

    private val _isLogged: MutableLiveData<Boolean> = MutableLiveData()
    val isLogged: LiveData<Boolean>
        get() = _isLogged

    fun initFirebase() {
        viewModelScope.launch {
            authService.initFirebase()
        }
    }

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _isLogged.value = authService.loginUser(email, password)
                _isError.value = authService.getIsErrorMessage()
            } catch (e: Exception) {
                _isError.value = e.message
                _isLogged.value = false
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _isLogged.value = authService.registerUser(email, password)
                _isError.value = authService.getIsErrorMessage()
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    fun getCurrentUser() = authService.getCurrentUser()
}