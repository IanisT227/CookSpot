package com.example.cookspot.feature.create_new_recipe

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.onNegative
import com.example.awesomedialog.onPositive
import com.example.cookspot.BuildConfig
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentCreateNewRecipeStepOneBinding
import com.example.cookspot.showAlerter
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import java.io.File

class FragmentCreateRecipeStepOne : Fragment(R.layout.fragment_create_new_recipe_step_one) {

    private val binding by viewBinding(FragmentCreateNewRecipeStepOneBinding::bind)
    private var photoStatus = false
    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    binding.uploadPhotoIV.setImageURI(uri)
                    photoStatus = true
                }
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                binding.uploadPhotoIV.setImageURI(uri)
                photoStatus = true
            }
        }

    private var latestTmpUri: Uri? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        if (binding.titleTIEE.text.isNullOrEmpty() || binding.durationTIEE.text.isNullOrEmpty() || binding.numberOfPersonsTIEE.text.isNullOrEmpty() || photoStatus == false)
            return false
        return true
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
                findNavController().navigate(FragmentCreateRecipeStepOneDirections.actionFragmentCreateRecipeStepOneToFragmentCreateRecipeStepTwo())
            else
                showAlerter("All fields must be completed", requireActivity())
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
            .onPositive(text = "Take picture") {
                takeImage()
            }
            .onNegative(text = "Choose from gallery") {
                selectImageFromGallery()
            }
    }

    companion object {
        const val PREFIX = "tmp_image_file"
        const val SUFIX = ".png"
    }
}