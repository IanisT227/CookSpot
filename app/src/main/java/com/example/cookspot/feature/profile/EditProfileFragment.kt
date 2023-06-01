package com.example.cookspot.feature.profile

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.onNegative
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.title
import com.example.cookspot.BuildConfig
import com.example.cookspot.R
import com.example.cookspot.checkName
import com.example.cookspot.checkUserOrPassword
import com.example.cookspot.databinding.FragmentEditProfileBinding
import com.example.cookspot.viewmodel.AuthenticationViewModel
import com.example.cookspot.feature.create_new_recipe.CreateRecipeStepOneFragment
import com.example.cookspot.loadProfilePhoto
import com.example.cookspot.logTag
import com.example.cookspot.model.User
import com.example.cookspot.showError
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.io.File

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {
    private val binding by viewBinding(FragmentEditProfileBinding::bind)
    private val authenticationViewModel: AuthenticationViewModel by activityViewModel()
    private var photoStatus = false
    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    binding.profilePictureCIV.setImageURI(uri)
                    latestTmpUri = uri
                    photoStatus = true
                }
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.profilePictureCIV.setImageURI(uri)
                latestTmpUri = uri
                photoStatus = true
            }
        }

    private var latestTmpUri: Uri? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authenticationViewModel.initFirebase()
        initObservers()
        authenticationViewModel.getCurrentUser()
        initButtons()
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile(
            CreateRecipeStepOneFragment.PREFIX,
            CreateRecipeStepOneFragment.SUFIX
        ).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(
            requireActivity().applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }

    private fun showDialog() {
        AwesomeDialog.build(requireActivity())
            .title(title = "Upload from:")
            .onPositive(text = "Take picture") {
                takeImage()
            }
            .onNegative(text = "Choose from gallery") {
                selectImageFromGallery()
            }
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
                showError(isError, requireActivity())
            }
        }

        authenticationViewModel.userData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                initViews(user)
            }
        }

        binding.profilePictureCIV.loadProfilePhoto(authenticationViewModel.userId.value)
    }

    private fun initButtons() {
        binding.saveProfileBtn.setOnClickListener {
            if (checkFields()) {
                authenticationViewModel.updateUser(
                    binding.usernameTIEE.text.toString(), binding.fullNameTIEE.text.toString()
                )
                if (photoStatus) {
                    authenticationViewModel.uploadProfilePicture(latestTmpUri !!)
                }
                Toast.makeText(requireContext(), "Profile updated!", Toast.LENGTH_SHORT).show()
            } else {
                showError("Fields cannot be empty", requireActivity())
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment())
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(EditProfileFragmentDirections.actionEditProfileFragmentToProfileFragment())
        }

        binding.profilePictureCIV.setOnClickListener { showDialog() }
    }

    private fun checkFields(): Boolean {
        return (checkName(binding.fullNameTIEE) || checkUserOrPassword(binding.usernameTIEE))
    }
}