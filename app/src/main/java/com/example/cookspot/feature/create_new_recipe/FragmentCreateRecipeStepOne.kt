package com.example.cookspot.feature.create_new_recipe

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import com.example.cookspot.databinding.FragmentCreateNewRecipeStepOneBinding
import com.example.cookspot.logTag
import com.example.cookspot.model.Recipe
import com.example.cookspot.showError
import com.example.cookspot.viewmodel.AuthenticationViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import java.io.File

class FragmentCreateRecipeStepOne : Fragment(R.layout.fragment_create_new_recipe_step_one) {

    private val binding by viewBinding(FragmentCreateNewRecipeStepOneBinding::bind)
    private val authenticationViewModel: AuthenticationViewModel by activityViewModel()
    private lateinit var currentUserId: String
    private var photoStatus = false
    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    binding.uploadPhotoIV.setImageURI(uri)
                    latestTmpUri = uri
                    photoStatus = true
                }
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.uploadPhotoIV.setImageURI(uri)
                latestTmpUri = uri
                photoStatus = true
            }
        }

    private var latestTmpUri: Uri? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authenticationViewModel.initFirebase()
        initButtons()
        initObservers()
        authenticationViewModel.getCurrentUserId()
        authenticationViewModel.getCurrentUser()
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
        val tmpFile = File.createTempFile(PREFIX, SUFIX).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(
            requireActivity().applicationContext,
            "${BuildConfig.APPLICATION_ID}.provider",
            tmpFile
        )
    }

    private fun checkRecipe(): Boolean {
        return !(binding.titleTIEE.text.isNullOrEmpty() || binding.durationTIEE.text.isNullOrEmpty() || binding.numberOfPersonsTIEE.text.isNullOrEmpty() || !photoStatus)
    }

    private fun initButtons() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(FragmentCreateRecipeStepOneDirections.actionGlobalFeedFragment())
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(FragmentCreateRecipeStepOneDirections.actionGlobalFeedFragment())
        }

        binding.nextStepBtn.setOnClickListener {
            if (checkRecipe())
                findNavController().navigate(
                    FragmentCreateRecipeStepOneDirections.actionFragmentCreateRecipeStepOneToFragmentCreateRecipeStepTwo(
                        Recipe(
                            name = binding.titleTIEE.text.toString(),
                            duration = binding.durationTIEE.text.toString(),
                            imageUri = latestTmpUri.toString(),
                            makes = binding.numberOfPersonsTIEE.text.toString(),
                            publisherId = currentUserId,
                            publisherName = authenticationViewModel.getCurrentUserUsername() !!
                        )
                    )
                )
            else
                showError("All fields must be completed", requireActivity())
        }

        binding.uploadPhotoIV.setOnClickListener {
            showDialog()
        }

        binding.deletePhotoIBtn.setOnClickListener {
            binding.uploadPhotoIV.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_add_photo
                )
            )
            photoStatus = false
        }
    }

    private fun showDialog() {
        AwesomeDialog.build(requireActivity())
            .title(title = "Upload from:", titleColor = R.color.dark_grey)
            .onPositive(text = "Take picture") {
                takeImage()
            }
            .onNegative(text = "Choose from gallery") {
                selectImageFromGallery()
            }
    }

    private fun initObservers() {
        authenticationViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            logTag("isLoadingValue", isLoading.toString())
            binding.isLoadingCIP.isVisible = isLoading
            binding.nextStepBtn.isEnabled = !isLoading
        }

        authenticationViewModel.userId.observe(viewLifecycleOwner) { userId ->
            currentUserId = userId
            logTag("userid", currentUserId)
        }
    }

    companion object {
        const val PREFIX = "tmp_image_file"
        const val SUFIX = ".png"
    }
}