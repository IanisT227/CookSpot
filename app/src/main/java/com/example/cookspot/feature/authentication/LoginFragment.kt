package com.example.cookspot.feature.authentication

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookspot.*
import com.example.cookspot.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel


class LoginFragment : Fragment(R.layout.fragment_login) {
    private val binding by viewBinding(FragmentLoginBinding::bind)
    private val authenticationViewModel: AuthenticationViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        authenticationViewModel.initFirebase()
        initButtons()
    }

    private fun initButtons() {
        binding.goBackLoginBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigateUp()
        }

        binding.loginBtn.setOnClickListener {
            if (!(checkMail(binding.emailTIEE))) {
                showAlerter("Invalid mail format", requireActivity())
            } else {
                if (!checkUserOrPassword(binding.passwordTIEE)) {
                    showAlerter("Password must have at least 8 characters!", requireActivity())
                } else {
                    authenticationViewModel.loginUser(
                        binding.emailTIEE.text.toString(),
                        binding.passwordTIEE.text.toString()
                    )
                }
            }
            binding.emailTIEE.text!!.clear()
            binding.passwordTIEE.text!!.clear()
        }
    }

    private fun initObservers() {
        authenticationViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            logTag("isLoadingValue", isLoading.toString())
            binding.isLoadingCIP.isVisible = isLoading
            binding.loginBtn.isEnabled = !isLoading
        }

        authenticationViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError != null) {
                logTag("isErrorValue", isError.toString())
                showAlerter(isError, requireActivity())
            }
        }

        authenticationViewModel.isLogged.observe(viewLifecycleOwner) { isLogged ->
            if (isLogged) {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToFeedFragment())
            }
        }
    }
}