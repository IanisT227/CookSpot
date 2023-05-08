package com.example.cookspot.feature.profile

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookspot.R
import com.example.cookspot.databinding.BottomNavigationLayoutBinding
import com.example.cookspot.databinding.FragmentProfileBinding
import com.example.cookspot.feature.authentication.AuthenticationViewModel
import com.example.cookspot.logTag
import com.example.cookspot.showAlerter
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val binding by viewBinding(FragmentProfileBinding::bind)
    private var doubleBackPressed = false
    private var bottomNavigationBarBinding: BottomNavigationLayoutBinding? = null
    private val authenticationViewModel: AuthenticationViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initButtons()
        initObservers()
        authenticationViewModel.getCurrentUser()
    }

    private fun initViews() {
        bottomNavigationBarBinding = binding.bottomNavigationBarCL

        bottomNavigationBarBinding!!.profileBtn.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary_orange
            )
        )

        binding.editProfileBtn.setOnClickListener {
            authenticationViewModel.getCurrentUser()
        }

        binding.userFullNameTV.text = authenticationViewModel.getCurrentUserFullName()
        binding.usernameTV.text = "@${authenticationViewModel.getCurrentUserUsername()}"
    }

    private fun initButtons() {
        binding.bottomNavigationBarCL.homeBtn.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToFeedFragment())
        }

        binding.bottomNavigationBarCL.addNewRecipeBtn.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToCreateRecipeNavigation())
        }

        binding.logOutBtn.setOnClickListener {
            authenticationViewModel.logOutUser()
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAuthenticationFragment())
        }

        binding.editProfileBtn.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment())
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToFeedFragment())
        }
    }

    private fun initObservers() {
        authenticationViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError != null) {
                logTag("isErrorValue", isError.toString())
                showAlerter(isError, requireActivity())
            }
        }

        authenticationViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.isLoadingCIP.isVisible = isLoading
            binding.editProfileBtn.isEnabled = ! isLoading
            binding.logOutBtn.isEnabled = ! isLoading
            logTag("isLoadingValue", isLoading.toString())
        }
    }
}