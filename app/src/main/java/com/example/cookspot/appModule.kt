package com.example.cookspot

import com.example.cookspot.viewmodel.AuthenticationViewModel
import com.example.cookspot.viewmodel.RecipeViewModel
import com.example.cookspot.repository.UserDataInternalStorageManager
import com.example.cookspot.service.AuthService
import com.example.cookspot.service.RecipeService
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val service = module {
    single { AuthService() }
    single { RecipeService() }
}

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
    viewModel {
        RecipeViewModel(
            recipeService = get()
        )
    }
}