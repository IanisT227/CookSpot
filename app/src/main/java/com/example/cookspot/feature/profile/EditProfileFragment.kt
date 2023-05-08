package com.example.cookspot.feature.profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookspot.R
import com.example.cookspot.checkName
import com.example.cookspot.checkUserOrPassword
import com.example.cookspot.databinding.FragmentEditProfileBinding
import com.example.cookspot.feature.authentication.AuthenticationViewModel
import com.example.cookspot.logTag
import com.example.cookspot.model.User
import com.example.cookspot.showAlerter
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    private val binding by viewBinding(FragmentEditProfileBinding::bind)
    private val authenticationViewModel: AuthenticationViewModel by activityViewModel()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authenticationViewModel.initFirebase()
        initObservers()
        authenticationViewModel.getCurrentUser()
        initButtons()

    }

    private fun initViews(user: User) {
        binding.emailValueTV.text = user.email
        binding.fullNameTIEE.setText(user.fullName)
        binding.usernameTIEE.setText(user.username)
    }

    private fun initObservers() {
        authenticationViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.isLoadingCIP.isVisible = isLoading
            binding.saveProfileBtn.isEnabled = ! isLoading
            logTag("isLoadingValue", isLoading.toString())
        }

        authenticationViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError != null) {
                logTag("isErrorValue", isError.toString())
                showAlerter(isError, requireActivity())
            }
        }

        authenticationViewModel.userData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                initViews(user)
            }
        }
    }

    private fun initButtons() {
        binding.saveProfileBtn.setOnClickListener {
            if (checkFields()) {
                authenticationViewModel.updateUser(
                    binding.usernameTIEE.text.toString(), binding.fullNameTIEE.text.toString()
                )

                Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show()
            } else {
                showAlerter("Fields cannot be empty", requireActivity())
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment())
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment())
        }
    }

    private fun checkFields(): Boolean {
        return (checkName(binding.fullNameTIEE) || checkUserOrPassword(binding.usernameTIEE))
    }
}