package com.example.cookspot.feature.authentication

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cookspot.model.User
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

    private val _userData: MutableLiveData<User?> = MutableLiveData()
    val userData: LiveData<User?>
        get() = _userData

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
                _isLogged.value = true
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
                internalStorageManager.setUserFullName("")
                internalStorageManager.setUserUsername("")
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
            _userId.value = authService.getCurrentUserId()
            _isError.value = authService.getIsErrorMessage()
        } catch (e: Exception) {
            internalStorageManager.setIsUserLoggedIn(true)
            _isError.value = e.message
        } finally {
            _isLoading.value = false
            _isError.value = null
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _userData.value = authService.getCurrentUser().receive()
                Log.v("userdatavm", _userData.value.toString())
                internalStorageManager.setUserUsername(_userData.value?.username.toString())
                internalStorageManager.setUserFullName(_userData.value?.fullName.toString())
                _isError.value = authService.getIsErrorMessage()
            } catch (e: Exception) {
                internalStorageManager.setIsUserLoggedIn(true)
                _isError.value = e.message
            } finally {
                _isLoading.value = false
                _isError.value = null
            }
        }
    }

    fun getCurrentUserFullName() = internalStorageManager.getUserFullName()

    fun getCurrentUserUsername() = internalStorageManager.getUserUsername()
}