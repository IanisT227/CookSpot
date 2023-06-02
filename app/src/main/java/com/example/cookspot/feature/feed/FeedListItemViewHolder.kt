package com.example.cookspot.feature.feed

import androidx.recyclerview.widget.RecyclerView
import com.example.cookspot.LIKE_RECIPE
import com.example.cookspot.R
import com.example.cookspot.SAVE_RECIPE
import com.example.cookspot.SHARE_RECIPE
import com.example.cookspot.VIEW_RECIPE
import com.example.cookspot.databinding.FeedListItemBinding
import com.example.cookspot.loadRecipePhoto
import com.example.cookspot.logTag
import com.example.cookspot.model.Recipe

class FeedListItemViewHolder(
    private val binding: FeedListItemBinding,
    private val onRecipeClickListener: OnRecipeClickListener
) : RecyclerView.ViewHolder(binding.root) {
    private lateinit var clickedRecipe: Recipe
    private var isLiked = false
    private var isSaved = false

    init {
        binding.recipePictureIV.setOnClickListener {
            clickedRecipe.let {
                onRecipeClickListener(
                    Pair(clickedRecipe, VIEW_RECIPE)
                )
            }
        }

        binding.likeBtn.setOnClickListener {
            clickedRecipe.let {
                onRecipeClickListener(
                    Pair(clickedRecipe, LIKE_RECIPE)
                )
                isLiked = if (! isLiked) {
                    binding.likeBtn.setImageResource(R.drawable.ic_heart_full)
                    true
                } else {
                    binding.likeBtn.setImageResource(R.drawable.ic_heart)
                    false
                }
            }
        }

        binding.saveRecipeIBtn.setOnClickListener {
            clickedRecipe.let {
                onRecipeClickListener(
                    Pair(clickedRecipe, SAVE_RECIPE))
                isSaved = if (! isSaved) {
                    binding.saveRecipeIBtn.setImageResource(R.drawable.ic_saved_full)
                    true
                } else {
                    binding.saveRecipeIBtn.setImageResource(R.drawable.ic_save)
                    false
                }
            }
        }

        binding.shareBtn.setOnClickListener {
            onRecipeClickListener(Pair(clickedRecipe, SHARE_RECIPE))
        }
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
    }
}