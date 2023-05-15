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
import com.example.cookspot.viewmodel.AuthenticationViewModel
import com.example.cookspot.logTag
import com.example.cookspot.showAlerter
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import kotlin.math.log

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val binding by viewBinding(FragmentProfileBinding::bind)
    private var doubleBackPressed = false
    private var bottomNavigationBarBinding: BottomNavigationLayoutBinding? = null
    private val authenticationViewModel: AuthenticationViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authenticationViewModel.initFirebase()
        initViews()
        initButtons()
        initObservers()
        authenticationViewModel.getCurrentUser()
        authenticationViewModel.getCurrentUserProfilePicture()
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

        authenticationViewModel.profilePictureUri.observe(viewLifecycleOwner) { pictureUri ->
            if (pictureUri != null)
                binding.profilePictureCIV.setImageURI(pictureUri)
        }

        authenticationViewModel.userData.observe(viewLifecycleOwner) { user ->
            logTag("profileValues", user.toString())
            logTag("profileValues", authenticationViewModel.getCurrentUserFullName().toString())
            logTag("profileValues",authenticationViewModel.getCurrentUserUsername().toString())
            if (authenticationViewModel.getCurrentUserFullName()
                    .isNullOrEmpty() || authenticationViewModel.getCurrentUserUsername()
                    .isNullOrEmpty()
            ) {
                binding.userFullNameTV.text = user!!.fullName
                binding.usernameTV.text = "@${user.username}"
            }
        }
    }
}