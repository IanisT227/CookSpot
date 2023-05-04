package com.example.cookspot.feature.create_new_recipe

import androidx.recyclerview.widget.RecyclerView
import com.example.cookspot.databinding.TagListItemBinding

class TagListItemViewHolder(
    private val binding: TagListItemBinding,
    private val onTagClickListener: OnTagClickListener
) : RecyclerView.ViewHolder(binding.root) {

}
