package com.example.cookspot

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CookSpotApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CookSpotApplication)
            modules(
                listOf(
                    service,
                    internalStorage,
                    viewModel
                )
            )
        }
    }
}