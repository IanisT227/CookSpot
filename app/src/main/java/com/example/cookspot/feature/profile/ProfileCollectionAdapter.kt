package com.example.cookspot.feature.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.cookspot.databinding.RecipeCollectionGridItemBinding
import com.example.cookspot.feature.feed.RecipeItemDiffUtilItemCallback
import com.example.cookspot.model.Recipe

typealias OnRecipeClickListener = (recipe: Recipe) -> Unit


class ProfileCollectionAdapter(
    private val onRecipeClickListener: OnRecipeClickListener
) :
    ListAdapter<Recipe, ProfileCollectionItemViewHolder>(ProfileRecipeItemDiffUtilItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ProfileCollectionItemViewHolder(
            RecipeCollectionGridItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            onRecipeClickListener
        )

    override fun onBindViewHolder(holder: ProfileCollectionItemViewHolder, position: Int) {
        if (getItem(position) != null)
            holder.bind(getItem(position))
    }
}