package com.example.cookspot.feature.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookspot.R
import com.example.cookspot.viewmodel.AuthenticationViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val authenticationViewModel: AuthenticationViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Handler(Looper.getMainLooper()).postDelayed({
            if (authenticationViewModel.getLoggedStatus()) {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToFeedFragment())
            } else {
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToAuthenticationFragment())
            }
        }, SPLASH_NAV_DELAY)
    }

    companion object {
        const val SPLASH_NAV_DELAY = 1500L
    }
}