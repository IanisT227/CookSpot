package com.example.cookspot.feature.create_new_recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.cookspot.databinding.TagListItemBinding


typealias OnTagClickListener = (tagId: String) -> Boolean

class TagListAdapter(private val onTagClickListener: OnTagClickListener) :
    ListAdapter<String, TagListItemViewHolder>(TagListItemDiffUtilItemCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TagListItemViewHolder(
        TagListItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ), onTagClickListener
    )

    override fun onBindViewHolder(holder: TagListItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}