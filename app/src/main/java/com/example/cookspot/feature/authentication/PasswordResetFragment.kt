package com.example.cookspot.feature.authentication

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookspot.R
import com.example.cookspot.checkMail
import com.example.cookspot.databinding.FragmentResetPasswordBinding
import com.example.cookspot.logTag
import com.example.cookspot.showError
import com.example.cookspot.showMailSent
import com.example.cookspot.viewmodel.AuthenticationViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class PasswordResetFragment : Fragment(R.layout.fragment_reset_password) {
    private val binding by viewBinding(FragmentResetPasswordBinding::bind)
    private val authenticationViewModel: AuthenticationViewModel by activityViewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authenticationViewModel.initFirebase()
        initObservers()
        initViews()
    }

    private fun initViews() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(PasswordResetFragmentDirections.actionPasswordResetFragment2ToLoginFragment())
        }

        binding.sendMailBtn.setOnClickListener {
            if (!(checkMail(binding.emailTIEE))) {
                showError("Invalid mail format", requireActivity())
            } else {
                authenticationViewModel.resetPassword(binding.emailTIEE.text.toString())
            }
        }
    }

    private fun initObservers() {
        authenticationViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            logTag("isLoadingValue", isLoading.toString())
            binding.isLoadingCIP.isVisible = isLoading
            binding.sendMailBtn.isEnabled = !isLoading
        }

        authenticationViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError != null) {
                logTag("isErrorValue", isError.toString())
                showError(isError, requireActivity())
            }
        }

        authenticationViewModel.isMailSent.observe(viewLifecycleOwner) { isMailSent ->
            if (isMailSent) {
                showMailSent(binding.emailTIEE.text.toString(), requireActivity())
            }
        }
    }
}