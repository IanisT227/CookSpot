package com.example.cookspot

import com.example.cookspot.feature.authentication.AuthenticationViewModel
import com.example.cookspot.repository.UserDataInternalStorageManager
import com.example.cookspot.service.AuthService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel


val service = module { single { AuthService() } }

val internalStorage = module {
    single { UserDataInternalStorageManager(androidContext()) }
}

val viewModel = module {
    viewModel {
        AuthenticationViewModel(
            authService = get(),
            internalStorageManager = get()
        )
    }
}