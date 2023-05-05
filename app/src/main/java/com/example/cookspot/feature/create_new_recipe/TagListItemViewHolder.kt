package com.example.cookspot.feature.create_new_recipe

import androidx.recyclerview.widget.RecyclerView
import com.example.cookspot.databinding.TagListItemBinding
import com.example.cookspot.logTag
import kotlin.math.log

class TagListItemViewHolder(
    private val binding: TagListItemBinding,
    private val onTagClickListener: OnTagClickListener
) : RecyclerView.ViewHolder(binding.root) {
    var itemTagValue: String? = null
    var isChecked: Boolean = false

    init {
        binding.radioButtonTagItem.setOnClickListener {
            onTagClickListener(itemTagValue!!)
            isChecked = !isChecked
            binding.radioButtonTagItem.setChecked(isChecked)
        }
    }

    fun bind(tagListItem: String) {
        itemTagValue = tagListItem
        isChecked = false
        binding.radioButtonTagItem.text = tagListItem
    }
}
