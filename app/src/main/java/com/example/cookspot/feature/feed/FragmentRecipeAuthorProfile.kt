package com.example.cookspot.feature.feed

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentRecipeAuthorProfileFragmentBinding
import com.example.cookspot.feature.profile.ProfileCollectionAdapter
import com.example.cookspot.feature.profile.ProfileFragment
import com.example.cookspot.loadUrl
import com.example.cookspot.model.Recipe
import com.example.cookspot.model.User
import com.example.cookspot.viewmodel.AuthenticationViewModel
import com.example.cookspot.viewmodel.RecipeViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FragmentRecipeAuthorProfile : Fragment(R.layout.fragment_recipe_author_profile_fragment) {
    private val binding by viewBinding(FragmentRecipeAuthorProfileFragmentBinding::bind)
    private val authenticationViewModel: AuthenticationViewModel by activityViewModel()
    private val recipeViewModel: RecipeViewModel by activityViewModel()
    private lateinit var profileListAdapter: ProfileCollectionAdapter
    private val recipeAuthorProfileFragmentArgs: FragmentRecipeAuthorProfileArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        authenticationViewModel.getUserById(recipeAuthorProfileFragmentArgs.userId)
        recipeViewModel.getPostedRecipes(recipeAuthorProfileFragmentArgs.userId)

    }

    private fun initViews(userData: User) {
        binding.usernameTV.text = "@${userData.username}"
        binding.userFullNameTV.text = userData.fullName
        binding.profilePictureCIV.loadUrl(userData.imageUri)

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(FragmentRecipeAuthorProfileDirections.actionRecipeAuthorProfileFragmentToFeedFragment())
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(FragmentRecipeAuthorProfileDirections.actionRecipeAuthorProfileFragmentToFeedFragment())
        }
    }

    private fun initObservers() {
        authenticationViewModel.userData.observe(viewLifecycleOwner) { userData ->
            if (userData != null) {
                initViews(userData)
            }

            recipeViewModel.postedRecipesList.observe(viewLifecycleOwner) { recipeList ->
                if (recipeList != null) {
                    initRecyclerView(
                        GridLayoutManager(
                            requireContext(),
                            ProfileFragment.GALLERY_SPAN_COUNT
                        ), recipeList
                    )
                }
            }

        }
    }

    private fun initRecyclerView(
        layoutManager: GridLayoutManager,
        recipeList: MutableList<Recipe>
    ) {
        profileListAdapter = ProfileCollectionAdapter(::onItemClickListener, recipeList)
        binding.recipeListRV.adapter = profileListAdapter
        profileListAdapter.submitList(recipeList)
        binding.recipeListRV.layoutManager = layoutManager
    }

    private fun onItemClickListener(recipe: Recipe) {
        findNavController().navigate(
            FragmentRecipeAuthorProfileDirections.actionRecipeAuthorProfileFragmentToFragmentViewFullRecipe(
                recipe
            )
        )
    }
}