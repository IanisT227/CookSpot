package com.example.cookspot.feature.feed

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cookspot.R
import com.example.cookspot.databinding.BottomNavigationLayoutBinding
import com.example.cookspot.databinding.FragmentFeedBinding
import com.example.cookspot.logTag
import com.example.cookspot.model.Recipe
import com.example.cookspot.showError
import com.example.cookspot.viewmodel.AuthenticationViewModel
import com.example.cookspot.viewmodel.RecipeViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FeedFragment : Fragment(R.layout.fragment_feed) {
    private val binding by viewBinding(FragmentFeedBinding::bind)
    private val authenticationViewModel: AuthenticationViewModel by activityViewModel()
    private val recipeViewModel: RecipeViewModel by activityViewModel()
    private var bottomNavigationBarBinding: BottomNavigationLayoutBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recipeViewModel.initFirebase()
        authenticationViewModel.initFirebase()
        initObservers()
        initViews()
        initButtons()
        authenticationViewModel.getCurrentUserId()
    }

    private fun initViews() {
        bottomNavigationBarBinding = binding.bottomNavigationBarCL

        bottomNavigationBarBinding!!.homeBtn.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary_orange
            )
        )
    }

    private fun initButtons() {
        binding.bottomNavigationBarCL.profileBtn.setOnClickListener {
            findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToProfileFragment())
        }

        binding.bottomNavigationBarCL.addNewRecipeBtn.setOnClickListener {
            findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToCreateRecipeNavigation())
        }

        binding.bottomNavigationBarCL.searchBtn.setOnClickListener {
            findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToFragmentSearch())
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            requireActivity().finish()
        }
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

        recipeViewModel.postedRecipesList.observe(viewLifecycleOwner) { recipeList ->
            logTag("recipelist", recipeList.toString())
            if (recipeList != null)
                initRecyclerView(
                    LinearLayoutManager(requireContext()),
                    recipeList
                )
        }

        authenticationViewModel.userId.observe(viewLifecycleOwner){ userId ->
            logTag("userId=", userId)
            if (userId != null) {
                authenticationViewModel.setCurrentUserId(userId)
                recipeViewModel.getPostedRecipes(userId)
            }
        }
    }

    private fun initRecyclerView(
        layoutManager: LinearLayoutManager,
        recipeList: List<Recipe>
    ) {
        val feedListAdapter = FeedListAdapter(::onItemClickListener)
        binding.feedRecipesRV.adapter = feedListAdapter
        feedListAdapter.submitList(recipeList.toMutableList())
        binding.feedRecipesRV.layoutManager = layoutManager
    }

    private fun onItemClickListener(recipe: Recipe) {
        findNavController().navigate(
            FeedFragmentDirections.actionFeedFragmentToFragmentViewFullRecipe(
                recipe
            )
        )
    }
}