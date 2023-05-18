package com.example.cookspot.feature.feed

import androidx.recyclerview.widget.DiffUtil
import com.example.cookspot.model.Recipe

class RecipeItemDiffUtilItemCallback: DiffUtil.ItemCallback<Recipe>() {
    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe) = oldItem == newItem

    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe) = oldItem == newItem
}