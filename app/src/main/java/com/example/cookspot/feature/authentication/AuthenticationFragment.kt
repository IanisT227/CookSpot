package com.example.cookspot.feature.authentication

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentAuthenticationBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class AuthenticationFragment : Fragment(R.layout.fragment_authentication) {
    private val binding by viewBinding(FragmentAuthenticationBinding::bind)
    private var doubleBackPressed = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtons()
    }

    private fun initButtons() {
        binding.loginBtn.setOnClickListener {
            doubleBackPressed = false
            findNavController().navigate(AuthenticationFragmentDirections.actionAuthenticationFragmentToLoginFragment())
        }

        binding.signUpBtn.setOnClickListener {
            doubleBackPressed = false
            findNavController().navigate(AuthenticationFragmentDirections.actionAuthenticationFragmentToRegisterFragment())
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            if (doubleBackPressed) {
                requireActivity().finish()
            } else {
                doubleBackPressed = true
                Toast.makeText(
                    requireContext(),
                    "Press again to exit",
                    Toast.LENGTH_SHORT
                ).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    doubleBackPressed = false
                }, ON_BACK_RESET_DURATION)
            }
        }
    }

    companion object {
        const val ON_BACK_RESET_DURATION = 3000L
    }
}