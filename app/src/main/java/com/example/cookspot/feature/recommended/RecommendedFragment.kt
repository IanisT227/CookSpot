package com.example.cookspot.feature.recommended

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cookspot.LIKE_RECIPE
import com.example.cookspot.R
import com.example.cookspot.SAVE_RECIPE
import com.example.cookspot.SHARE_RECIPE
import com.example.cookspot.VIEW_RECIPE
import com.example.cookspot.databinding.BottomNavigationLayoutBinding
import com.example.cookspot.databinding.FragmentFeedBinding
import com.example.cookspot.feature.feed.FeedFragmentDirections
import com.example.cookspot.feature.feed.FeedListAdapter
import com.example.cookspot.logTag
import com.example.cookspot.model.Recipe
import com.example.cookspot.model.RecipeStatus
import com.example.cookspot.shareRecipe
import com.example.cookspot.showError
import com.example.cookspot.viewmodel.AuthenticationViewModel
import com.example.cookspot.viewmodel.RecipeViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class RecommendedFragment : Fragment(R.layout.fragment_feed) {
    private val binding by viewBinding(FragmentFeedBinding::bind)
    private val authenticationViewModel: AuthenticationViewModel by activityViewModel()
    private val recipeViewModel: RecipeViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipeViewModel.initFirebase()
        authenticationViewModel.initFirebase()
        initObservers()
        initViews()
        authenticationViewModel.getCurrentUserId()
    }

    private fun initObservers() {
        recipeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.isLoadingCIP.isVisible = isLoading
            logTag("isLoadingValue", isLoading.toString())
        }

        recipeViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError != null) {
                logTag("isErrorValue", isError.toString())
                showError(isError, requireActivity())
            }
        }

        recipeViewModel.recommendedRecipesList.observe(viewLifecycleOwner) { recipeList ->
            logTag("recipelist", recipeList.toString())
            if (recipeList != null) {
                val updatedRecipeList: MutableList<Pair<Recipe, RecipeStatus>> = mutableListOf()
                for (recipe in recipeList) {
                    updatedRecipeList.add(Pair(recipe!!, RecipeStatus(
                        isLiked = false,
                        isSaved = false
                    )))
                }

                initRecyclerView(
                    LinearLayoutManager(requireContext()),
                    updatedRecipeList
                )
            }
        }

        authenticationViewModel.userId.observe(viewLifecycleOwner) { userId ->
            logTag("userId=", userId)
            if (userId != null) {
                authenticationViewModel.setCurrentUserId(userId)
                recipeViewModel.getRecommendedRecipeList()
            }
        }
    }

    private fun initRecyclerView(
        layoutManager: LinearLayoutManager,
        recipeList: MutableList<Pair<Recipe, RecipeStatus>>
    ) {
        val feedListAdapter = FeedListAdapter(::onItemClickListener)
        binding.feedRecipesRV.adapter = feedListAdapter
        feedListAdapter.submitList(recipeList)
        binding.feedRecipesRV.layoutManager = layoutManager
    }

    private fun onItemClickListener(pair: Pair<Recipe, String>) {
        when (pair.second) {
            VIEW_RECIPE -> {
                findNavController().navigate(
                    RecommendedFragmentDirections.actionRecommendedFragmentToFragmentViewFullRecipe(
                        pair.first
                    )
                )
            }

            LIKE_RECIPE -> {
                recipeViewModel.handleLike(pair.first.imageUri)
            }

            SAVE_RECIPE -> {
                recipeViewModel.handleSave(pair.first.imageUri)
            }

            SHARE_RECIPE -> {
                shareRecipe(pair.first, requireContext())
            }
        }
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

        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finish()
        }

    }
}