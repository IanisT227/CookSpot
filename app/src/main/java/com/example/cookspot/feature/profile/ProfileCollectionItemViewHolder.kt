package com.example.cookspot.feature.profile

import androidx.recyclerview.widget.RecyclerView
import com.example.cookspot.databinding.RecipeCollectionGridItemBinding
import com.example.cookspot.loadUrl
import com.example.cookspot.model.Recipe

class ProfileCollectionItemViewHolder(
    private val binding: RecipeCollectionGridItemBinding,
    private val onRecipeClickListener: OnRecipeClickListener
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var clickedRecipe: Recipe

    init {
        binding.recipePreviewIV.setOnClickListener { onRecipeClickListener(clickedRecipe) }
    }

    fun bind(recipe: Recipe) {
        clickedRecipe = recipe
        binding.recipePreviewTitleTV.text = recipe.name
        binding.recipePreviewIV.loadUrl(recipe.imageUri)
    }
}
