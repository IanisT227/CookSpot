package com.example.cookspot.feature.recommended

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentFeedBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class RecommendedFragment : Fragment(R.layout.fragment_feed) {
    private val binding by viewBinding(FragmentFeedBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        binding.bottomNavigationBarCL.recommendedBtn.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary_orange
            )
        )

        binding.bottomNavigationBarCL.homeBtn.setOnClickListener {
            findNavController().navigate(RecommendedFragmentDirections.actionRecommendedFragmentToFeedFragment())
        }

        binding.bottomNavigationBarCL.addNewRecipeBtn.setOnClickListener {
            findNavController().navigate(RecommendedFragmentDirections.actionRecommendedFragmentToCreateRecipeNavigation())
        }

        binding.bottomNavigationBarCL.searchBtn.setOnClickListener {
            findNavController().navigate(RecommendedFragmentDirections.actionRecommendedFragmentToFragmentSearch())
        }

        binding.bottomNavigationBarCL.profileBtn.setOnClickListener {
            findNavController().navigate(RecommendedFragmentDirections.actionRecommendedFragmentToUserProfile())
        }
    }
}