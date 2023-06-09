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
import com.example.cookspot.loadProfilePhoto
import com.example.cookspot.model.Recipe
import com.example.cookspot.model.RecipeStatus
import com.example.cookspot.model.User
import com.example.cookspot.showFollowerAlerter
import com.example.cookspot.viewmodel.AuthenticationViewModel
import com.example.cookspot.viewmodel.RecipeViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class RecipeAuthorProfileFragment : Fragment(R.layout.fragment_recipe_author_profile_fragment) {
    private val binding by viewBinding(FragmentRecipeAuthorProfileFragmentBinding::bind)
    private val authenticationViewModel: AuthenticationViewModel by activityViewModel()
    private val recipeViewModel: RecipeViewModel by activityViewModel()
    private lateinit var profileListAdapter: ProfileCollectionAdapter
    private val recipeAuthorProfileFragmentArgs: RecipeAuthorProfileFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObservers()
        authenticationViewModel.getUserById(recipeAuthorProfileFragmentArgs.userId)
        recipeViewModel.getPublishedRecipeList(recipeAuthorProfileFragmentArgs.userId)

    }

    private fun initViews(userData: User) {
        binding.usernameTV.text = "@${userData.username}"
        binding.userFullNameTV.text = userData.fullName
        binding.profilePictureCIV.loadProfilePhoto(recipeAuthorProfileFragmentArgs.userId)

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(RecipeAuthorProfileFragmentDirections.actionRecipeAuthorProfileFragmentToFeedFragment())
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigate(RecipeAuthorProfileFragmentDirections.actionRecipeAuthorProfileFragmentToFeedFragment())
        }

        binding.followUserProfile.setOnClickListener {
            authenticationViewModel.followUser(recipeAuthorProfileFragmentArgs.userId)
            showFollowerAlerter(requireActivity())
        }
    }

    private fun initObservers() {
        authenticationViewModel.userData.observe(viewLifecycleOwner) { userData ->
            if (userData != null) {
                initViews(userData)
            }

            recipeViewModel.publishedRecipesForUser.observe(viewLifecycleOwner) { recipeList ->
                if (recipeList != null) {
                    binding.postedNumberTV.text = recipeList.size.toString()
                    initRecyclerView(
                        GridLayoutManager(
                            requireContext(),
                            ProfileFragment.GALLERY_SPAN_COUNT
                        ), recipeList.toList() as List<Recipe>
                    )
                }
            }

        }
    }

    private fun initRecyclerView(
        layoutManager: GridLayoutManager,
        recipeList: List<Recipe>
    ) {
        profileListAdapter = ProfileCollectionAdapter(::onItemClickListener)
        binding.recipeListRV.adapter = profileListAdapter
        profileListAdapter.submitList(recipeList)
        binding.recipeListRV.layoutManager = layoutManager
    }

    private fun onItemClickListener(recipe: Recipe) {
        findNavController().navigate(
            RecipeAuthorProfileFragmentDirections.actionRecipeAuthorProfileFragmentToFragmentViewFullRecipe(
                recipe
            )
        )
    }
}