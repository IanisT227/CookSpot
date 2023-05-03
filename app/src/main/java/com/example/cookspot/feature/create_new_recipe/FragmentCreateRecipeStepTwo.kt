package com.example.cookspot.feature.create_new_recipe

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentCreateNewRecipeStepTwoBinding
import com.example.cookspot.model.Recipe
import com.example.cookspot.showAlerter
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FragmentCreateRecipeStepTwo : Fragment(R.layout.fragment_create_new_recipe_step_two) {

    private val recipeViewModel: RecipeViewModel by activityViewModel()
    private val binding by viewBinding(FragmentCreateNewRecipeStepTwoBinding::bind)
    private val args: FragmentCreateRecipeStepTwoArgs by navArgs()
    private var difficultyLevel: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v("arguments", args.newRecipe.toString())
        initButtons()
        recipeViewModel.initFirebase()
        recipeViewModel.getTags()
    }

    private fun initButtons() {
        binding.radioEasyBtn.setOnClickListener {
            difficultyLevel = "Easy"
        }

        binding.radioMediumBtn.setOnClickListener {
            difficultyLevel = "Medium"
        }

        binding.radioHardBtn.setOnClickListener {
            difficultyLevel = "Hard"
        }

        binding.nextStepBtn.setOnClickListener {
            if (difficultyLevel == null) {
                showAlerter("Please choose a difficulty level", requireActivity())
            } else {
                val recipe = Recipe(
                    name = args.newRecipe!!.name,
                    duration = args.newRecipe!!.duration,
                    imageUri = args.newRecipe!!.imageUri,
                    makes = args.newRecipe!!.makes,
                    difficulty = difficultyLevel!!
                )
                findNavController().navigate(
                    FragmentCreateRecipeStepTwoDirections.actionFragmentCreateRecipeStepTwoToFragmentCreateRecipeStepThree(
                        recipe
                    )
                )
            }

        }
    }
}