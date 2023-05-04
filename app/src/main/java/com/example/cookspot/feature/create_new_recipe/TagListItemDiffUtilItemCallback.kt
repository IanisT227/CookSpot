package com.example.cookspot.feature.create_new_recipe

import androidx.recyclerview.widget.DiffUtil

class TagListItemDiffUtilItemCallback : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem

    override fun areContentsTheSame(oldItem: String, newItem: String)= oldItem == newItem

}
