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
import com.example.cookspot.showRecipeAlerter
import com.example.cookspot.viewmodel.RecipeViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FragmentViewFullRecipe : Fragment(R.layout.fragment_view_full_recipe) {
    private val args: FragmentViewFullRecipeArgs by navArgs()
    private val binding by viewBinding(FragmentViewFullRecipeBinding::bind)
    private val recipeViewModel: RecipeViewModel by activityViewModel()
    private var isLiked = false
    private var isSaved = false
    private var isCooked = false

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
            if (! isCooked) {
                recipeViewModel.addToCooked(args.recipe.imageUri)
                showRecipeAlerter("Recipe added to Cooked collection", requireActivity())
                isCooked = true
                binding.markAsCookedRecipeBtn.text = "Remove from cooked"
            } else {
                recipeViewModel.removeFromCooked(args.recipe.imageUri)
                isCooked = false
                binding.markAsCookedRecipeBtn.text = "Mark as cooked"
            }

        }

        binding.saveRecipeIBtn.setOnClickListener {
            if (! isSaved) {
                recipeViewModel.addToSaved(args.recipe.imageUri)
                showRecipeAlerter("Recipe added to saved collection", requireActivity())
                isSaved = true
                binding.saveRecipeIBtn.setImageResource(R.drawable.ic_saved_full)
            } else {
                recipeViewModel.removeFromSaved(args.recipe.imageUri)
                isSaved = false
                binding.saveRecipeIBtn.setImageResource(R.drawable.ic_save)
            }
        }

        binding.recipeAuthorTV.setOnClickListener {
            findNavController().navigate(
                FragmentViewFullRecipeDirections.actionFragmentViewFullRecipeToRecipeAuthorProfileFragment(
                    args.recipe.publisherId
                )
            )
        }

        binding.likeBtn.setOnClickListener {
            if (! isLiked) {
                recipeViewModel.likeRecipe(args.recipe.imageUri)
                binding.likesNumberTV.text = (getLikesNumber() + 1).toString()
                binding.likeBtn.setImageResource(R.drawable.ic_heart_full)
                isLiked = true
            } else {
                recipeViewModel.unlikeRecipe(args.recipe.imageUri)
                binding.likesNumberTV.text = (getLikesNumber() - 1).toString()
                binding.likeBtn.setImageResource(R.drawable.ic_heart)
                isLiked = false
            }
        }
    }

    private fun getLikesNumber() = binding.likesNumberTV.text.toString().toInt()
}