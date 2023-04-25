package com.example.cookspot.feature.create_new_recipe

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentCreateNewRecipeStepOneBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class FragmentCreateRecipeStepOne : Fragment(R.layout.fragment_create_new_recipe_step_one) {

    private val binding by viewBinding(FragmentCreateNewRecipeStepOneBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initButtons()
    }

    private fun initButtons() {
        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(FragmentCreateRecipeStepOneDirections.actionGlobalFeedFragment())
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(FragmentCreateRecipeStepOneDirections.actionGlobalFeedFragment())
        }
    }
}