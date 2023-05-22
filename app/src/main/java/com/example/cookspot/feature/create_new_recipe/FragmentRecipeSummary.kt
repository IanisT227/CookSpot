package com.example.cookspot.feature.create_new_recipe

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.body
import com.example.awesomedialog.onNegative
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.title
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentRecipeSummaryBinding
import com.example.cookspot.logTag
import com.example.cookspot.showError
import com.example.cookspot.viewmodel.RecipeViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FragmentRecipeSummary : Fragment(R.layout.fragment_recipe_summary) {
    private val binding by viewBinding(FragmentRecipeSummaryBinding::bind)
    private val recipeViewModel: RecipeViewModel by activityViewModel()
    private val recipeArgs: FragmentRecipeSummaryArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipeViewModel.initFirebase()
        initViews()
        initButtons()
        initObservers()
    }

    private fun initViews() {
        val receivedRecipe = recipeArgs.finalRecipe
        binding.recipePictureIV.setImageURI(receivedRecipe.imageUri.toUri())
        binding.recipeNameTV.text = receivedRecipe.name
        binding.recipeAuthorTV.text = "@${receivedRecipe.publisherName}"
        binding.recipeDifficultyTV.text = receivedRecipe.difficulty
        binding.recipeDurationTV.text = receivedRecipe.duration
        binding.recipeMakesTV.text = receivedRecipe.makes
        binding.recipeTagOneBtn.text = receivedRecipe.tags[0]
        binding.recipeTagTwoBtn.text = receivedRecipe.tags[1]
        binding.recipeTagThreeBtn.text = receivedRecipe.tags[2]
        binding.ingredientsListTV.text = receivedRecipe.ingredients
        binding.cookingProcessParagraphTV.text = receivedRecipe.cookingProcess
    }

    private fun initButtons() {
        binding.toolbar.setNavigationOnClickListener { showDialog() }

        requireActivity().onBackPressedDispatcher.addCallback {
            showDialog()
        }

        binding.postRecipeBtn.setOnClickListener {
            recipeViewModel.uploadRecipe(recipeArgs.finalRecipe)
        }
    }

    private fun showDialog() {
        AwesomeDialog.build(requireActivity())
            .title(title = "Discard recipe?", titleColor = R.color.neutral_black)
            .body(
                body = "If you go back, your progress will be deleted",
                color = R.color.neutral_black
            )
            .onPositive(text = "Discard") {
                findNavController().navigate(
                    FragmentCreateRecipeStepTwoDirections.actionGlobalFeedFragment()
                )
            }
            .onNegative(text = "Cancel") {
            }
    }

    private fun initObservers() {
        recipeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            logTag("isLoadingValue", isLoading.toString())
            binding.isLoadingCIP.isVisible = isLoading
            binding.postRecipeBtn.isEnabled = !isLoading
        }

        recipeViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError != null) {
                logTag("isErrorValue", isError.toString())
                showError(isError, requireActivity())
            }
        }

        recipeViewModel.isPosted.observe(viewLifecycleOwner) { isPosted ->
            if (isPosted)
                findNavController().navigate(FragmentRecipeSummaryDirections.actionGlobalFeedFragment())
        }
    }
}