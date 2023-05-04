package com.example.cookspot.feature.create_new_recipe

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentRecipeDetailBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class RecipeSummaryFragment : Fragment(R.layout.fragment_recipe_detail) {
    private val binding by viewBinding(FragmentRecipeDetailBinding::bind)
    private val recipeViewModel: RecipeViewModel by activityViewModel()
    private val recipeArgs: RecipeSummaryFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        val receivedRecipe = recipeArgs.finalRecipe
        binding.recipePictureIV.setImageURI(receivedRecipe.imageUri)
        binding.recipeNameTV.text = receivedRecipe.name
        binding.recipeAuthorTV.text = "@${recipeViewModel.getUserName()}"
        binding.recipeDifficultyTV.text = receivedRecipe.difficulty
        binding.recipeDurationTV.text = receivedRecipe.duration
        binding.recipeMakesTV.text = receivedRecipe.makes
//        binding.recipeTagOneBtn.text = receivedRecipe.tags[0]
//        binding.recipeTagTwoBtn.text = receivedRecipe.tags[1]
//        binding.recipeTagThreeBtn.text = receivedRecipe.tags[2]
        binding.ingredientsListTV.text = receivedRecipe.ingredients
        binding.cookingProcessParagraphTV.text = receivedRecipe.cookingProcess
    }
}