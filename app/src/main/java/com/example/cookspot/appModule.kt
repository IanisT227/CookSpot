package com.example.cookspot

import com.example.cookspot.feature.authentication.AuthenticationViewModel
import com.example.cookspot.service.AuthService
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel


val service = module { single { AuthService() } }

val viewModel = module { viewModel { AuthenticationViewModel(authService = get()) } }