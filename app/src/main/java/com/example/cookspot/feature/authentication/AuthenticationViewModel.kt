package com.example.cookspot.feature.authentication

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookspot.repository.UserDataInternalStorageManager
import com.example.cookspot.service.AuthService
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    private val authService: AuthService,
    private val internalStorageManager: UserDataInternalStorageManager
) : ViewModel() {

    private val _isLoading: MutableLiveData<Boolean> = MutableLiveData()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _isError: MutableLiveData<String?> = MutableLiveData()
    val isError: LiveData<String?>
        get() = _isError

    private val _isLogged: MutableLiveData<Boolean> = MutableLiveData()
    val isLogged: LiveData<Boolean>
        get() = _isLogged

    private val _userId: MutableLiveData<String> = MutableLiveData()
    val userId: LiveData<String>
        get() = _userId

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

    fun registerUser(email: String, password: String, username: String, fullName: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _isLogged.value = authService.registerUser(email, password, username, fullName)
                _isError.value = authService.getIsErrorMessage()
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    fun logOutUser() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                authService.logOutUser()
                _isError.value = authService.getIsErrorMessage()
                internalStorageManager.setIsUserLoggedIn(false)
            } catch (e: Exception) {
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    fun setLoggedStatus(isLogged: Boolean) = internalStorageManager.setIsUserLoggedIn(isLogged)

    fun getLoggedStatus() = internalStorageManager.getIsUserLoggedIn()

    fun getCurrentUserId() = viewModelScope.launch {
        try {
            _isLoading.value = true
            authService.getCurrentUserId()
            _isError.value = authService.getIsErrorMessage()
            internalStorageManager.setIsUserLoggedIn(false)
        } catch (e: Exception) {
            _isError.value = e.message
        } finally {
            _isLoading.value = false
            _isError.value = null
        }
    }
}