package com.example.cookspot.feature.create_new_recipe

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter


typealias OnTagClickListener = (tagId: Int) -> Unit

class TagListAdapter(private val onTagClickListener: OnTagClickListener) :
    ListAdapter<String, TagListItemViewHolder>(TagListItemDiffUtilItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagListItemViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: TagListItemViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}