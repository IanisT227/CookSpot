package com.example.cookspot.feature.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentRecipeDetailBinding
import com.example.cookspot.feature.create_new_recipe.RecipeViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class RecipeDetailFragment : Fragment(R.layout.fragment_recipe_detail) {
    private val binding by viewBinding(FragmentRecipeDetailBinding::bind)
    private val recipeViewModel: RecipeViewModel by activityViewModel()
    private val recipeArgs: RecipeDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}