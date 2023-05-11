package com.example.cookspot.feature.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.cookspot.databinding.FeedListItemBinding
import com.example.cookspot.model.Recipe


typealias OnRecipeClickListener = (tagId: String) -> Unit

class FeedListAdapter(private val onRecipeClickListener: OnRecipeClickListener) :
    ListAdapter<Recipe, FeedListItemViewHolder>(FeedListItemDiffUtilItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FeedListItemViewHolder(
        FeedListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        onRecipeClickListener
    )

    override fun onBindViewHolder(holder: FeedListItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}