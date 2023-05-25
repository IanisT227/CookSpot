package com.example.cookspot.feature.feed

import androidx.recyclerview.widget.RecyclerView
import com.example.cookspot.databinding.FeedListItemBinding
import com.example.cookspot.loadRecipePhoto
import com.example.cookspot.logTag
import com.example.cookspot.model.Recipe

class FeedListItemViewHolder(
    private val binding: FeedListItemBinding,
    private val onRecipeClickListener: OnRecipeClickListener
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var clickedRecipe: Recipe

    init {
        binding.recipePictureIV.setOnClickListener { onRecipeClickListener(clickedRecipe) }
    }

    fun bind(recipe: Recipe) {
        clickedRecipe = recipe
        binding.recipePictureIV.loadRecipePhoto(recipe.imageUri)
        binding.recipeNameTV.text = recipe.name
        binding.recipeAuthorTV.text = "@${recipe.publisherName}"
        binding.recipeDifficultyTV.text = recipe.difficulty
        binding.recipeMakesTV.text = recipe.makes
        binding.recipeDurationTV.text = recipe.duration
        binding.recipeTagOneBtn.text = recipe.tags[0]
        binding.recipeTagTwoBtn.text = recipe.tags[1]
        binding.recipeTagThreeBtn.text = recipe.tags[2]
        logTag("likenumbers", recipe.likes.toString())
        binding.likesNumberTV.text = recipe.likes.toString()
    }
}