package com.example.cookspot.feature.create_new_recipe

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentCreateNewRecipeStepTwoBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FragmentCreateRecipeStepTwo : Fragment(R.layout.fragment_create_new_recipe_step_two) {

    private val recipeViewModel: RecipeViewModel by activityViewModel()
    private val binding by viewBinding(FragmentCreateNewRecipeStepTwoBinding::bind)
    private val args: FragmentCreateRecipeStepTwoArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v("arguments", args.newRecipe.toString())

        recipeViewModel.initFirebase()
        recipeViewModel.getTags()
    }

    private fun initButtons() {
        binding.radioEasyBtn.setOnClickListener {

        }

        binding.nextStepBtn.setOnClickListener {
            findNavController().navigate(
                FragmentCreateRecipeStepTwoDirections.actionFragmentCreateRecipeStepTwoToFragmentCreateRecipeStepThree(
                    args.newRecipe
                )
            )
        }
    }
}