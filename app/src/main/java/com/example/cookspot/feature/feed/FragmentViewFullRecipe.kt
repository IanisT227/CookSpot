package com.example.cookspot.feature.feed

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentViewFullRecipeBinding
import com.example.cookspot.loadUrl
import com.example.cookspot.viewmodel.RecipeViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FragmentViewFullRecipe : Fragment(R.layout.fragment_view_full_recipe) {
    private val args: FragmentViewFullRecipeArgs by navArgs()
    private val binding by viewBinding(FragmentViewFullRecipeBinding::bind)
    private val recipeViewModel: RecipeViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initButtons()
    }

    private fun initViews() {
        val receivedRecipe = args.recipe
        binding.recipeNameTV.text = receivedRecipe.name
        binding.recipeAuthorTV.text = "@${receivedRecipe.publisherName}"
        binding.recipeDifficultyTV.text = receivedRecipe.difficulty
        binding.recipeDurationTV.text = receivedRecipe.duration
        binding.recipeMakesTV.text = receivedRecipe.makes
        binding.recipeTagOneBtn.text = receivedRecipe.tags[0]
        binding.recipeTagTwoBtn.text = receivedRecipe.tags[1]
        binding.recipeTagThreeBtn.text = receivedRecipe.tags[2]
        binding.ingredientsListTV.text = receivedRecipe.ingredients
        binding.likesNumberTV.text = receivedRecipe.likes.toString()
        binding.cookingProcessParagraphTV.text = receivedRecipe.cookingProcess
        binding.recipePictureIV.loadUrl(receivedRecipe.imageUri)
    }

    private fun initButtons() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(
                FragmentViewFullRecipeDirections.actionFragmentViewFullRecipeToFeedFragment()
            )
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(
                FragmentViewFullRecipeDirections.actionFragmentViewFullRecipeToFeedFragment()
            )
        }

        binding.markAsCookedRecipeBtn.setOnClickListener {
            recipeViewModel.addToCooked(args.recipe.imageUri)
        }
    }

}