package com.example.cookspot.feature.feed

import androidx.recyclerview.widget.DiffUtil
import com.example.cookspot.model.Recipe
import com.example.cookspot.model.RecipeStatus

class RecipeItemDiffUtilItemCallback : DiffUtil.ItemCallback<Pair<Recipe, RecipeStatus>>() {
    override fun areItemsTheSame(
        oldItem: Pair<Recipe, RecipeStatus>,
        newItem: Pair<Recipe, RecipeStatus>
    ) = oldItem == newItem

    override fun areContentsTheSame(
        oldItem: Pair<Recipe, RecipeStatus>,
        newItem: Pair<Recipe, RecipeStatus>
    ) = oldItem == newItem
}