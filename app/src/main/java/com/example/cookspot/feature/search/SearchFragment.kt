package com.example.cookspot.feature.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
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
import com.example.cookspot.databinding.FragmentSearchBinding
import com.example.cookspot.feature.feed.FeedFragmentDirections
import com.example.cookspot.feature.feed.FeedListAdapter
import com.example.cookspot.hideKeyboard
import com.example.cookspot.logTag
import com.example.cookspot.model.Recipe
import com.example.cookspot.model.RecipeStatus
import com.example.cookspot.shareRecipe
import com.example.cookspot.showError
import com.example.cookspot.viewmodel.RecipeViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class SearchFragment : Fragment(R.layout.fragment_search) {
    private val binding by viewBinding(FragmentSearchBinding::bind)
    private val recipeViewModel: RecipeViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservables()
    }

    private fun initViews() {
        binding.bottomNavigationBarCL.searchBtn.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary_orange
            )
        )

        binding.bottomNavigationBarCL.homeBtn.setOnClickListener {
            findNavController().navigate(
                SearchFragmentDirections.actionFragmentSearchToFeedFragment()
            )
        }

        binding.bottomNavigationBarCL.addNewRecipeBtn.setOnClickListener {
            findNavController().navigate(
                SearchFragmentDirections.actionFragmentSearchToCreateRecipeNavigation()
            )
        }

        binding.bottomNavigationBarCL.profileBtn.setOnClickListener {
            findNavController().navigate(
                SearchFragmentDirections.actionFragmentSearchToUserProfile()
            )
        }

        binding.bottomNavigationBarCL.recommendedBtn.setOnClickListener {
            findNavController().navigate(SearchFragmentDirections.actionFragmentSearchToRecommendedFragment())
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(SearchFragmentDirections.actionFragmentSearchToFeedFragment())
        }

        binding.searchTIET.hint = "Search..."

        binding.searchTIET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.toString().isNotEmpty()) {
                    binding.searchTIET.hint = ""
                } else {
                    binding.searchTIET.hint = "Search..."
                }
            }
        })

        binding.searchTIET.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                recipeViewModel.searchRecipes(binding.searchTIET.text.toString())
                binding.searchBtn.hideKeyboard(binding.searchTIET, requireActivity())
                true
            } else {
                false
            }
        }

        binding.searchBtn.setOnClickListener {
            logTag("searchResults", "reached click")
            if (! binding.searchTIET.text.isNullOrBlank()) {
                logTag("searchResults", "reached click with search")
                recipeViewModel.searchRecipes(binding.searchTIET.text.toString())
            }
            binding.searchBtn.hideKeyboard(binding.searchTIET, requireActivity())
        }
    }

    private fun initObservables() {
        recipeViewModel.searchedRecipes.observe(viewLifecycleOwner) { searchResults ->
            if (searchResults != null && searchResults.size != 0) {
                initRecyclerView(
                    LinearLayoutManager(requireContext()),
                    searchResults
                )
            } else {
                Toast.makeText(requireContext(), "No recipes found", Toast.LENGTH_SHORT).show()
            }
        }

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
    }

    private fun initRecyclerView(
        layoutManager: LinearLayoutManager, recipeList: MutableList<Pair<Recipe, RecipeStatus>>
    ) {
        val feedListAdapter = FeedListAdapter(::onItemClickListener)
        binding.feedRecipesRV.adapter = feedListAdapter
        feedListAdapter.submitList(recipeList.toMutableList())
        binding.feedRecipesRV.layoutManager = layoutManager
    }

    private fun onItemClickListener(pair: Pair<Recipe, String>) {
        when (pair.second) {
            VIEW_RECIPE -> {
                findNavController().navigate(
                    SearchFragmentDirections.actionFragmentSearchToFragmentViewFullRecipe(
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
}