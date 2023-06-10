package com.example.cookspot.feature.profile

import androidx.recyclerview.widget.DiffUtil
import com.example.cookspot.model.Recipe
import com.example.cookspot.model.RecipeStatus

class ProfileRecipeItemDiffUtilItemCallback : DiffUtil.ItemCallback<Recipe>() {
    override fun areItemsTheSame(
        oldItem: Recipe,
        newItem: Recipe
    ) = oldItem == newItem

    override fun areContentsTheSame(
        oldItem: Recipe,
        newItem: Recipe
    ) = oldItem == newItem
}