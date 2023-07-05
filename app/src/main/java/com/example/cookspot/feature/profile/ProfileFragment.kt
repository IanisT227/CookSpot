package com.example.cookspot.feature.profile

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cookspot.R
import com.example.cookspot.databinding.BottomNavigationLayoutBinding
import com.example.cookspot.databinding.FragmentProfileBinding
import com.example.cookspot.loadProfilePhoto
import com.example.cookspot.logTag
import com.example.cookspot.model.Recipe
import com.example.cookspot.showError
import com.example.cookspot.viewmodel.AuthenticationViewModel
import com.example.cookspot.viewmodel.RecipeViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val binding by viewBinding(FragmentProfileBinding::bind)
    private var bottomNavigationBarBinding: BottomNavigationLayoutBinding? = null
    private val authenticationViewModel: AuthenticationViewModel by activityViewModel()
    private val recipeViewModel: RecipeViewModel by activityViewModel()
    private lateinit var profileListAdapter: ProfileCollectionAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authenticationViewModel.initFirebase()
        initViews()
        initButtons()
        initObservers()
        authenticationViewModel.getCurrentUser()
        recipeViewModel.getPublishedRecipeList(authenticationViewModel.userId.value!!)
        recipeViewModel.getRecipesListSize()
    }

    private fun initViews() {
        bottomNavigationBarBinding = binding.bottomNavigationBarCL

        bottomNavigationBarBinding!!.profileBtn.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary_orange
            )
        )

        binding.editProfileBtn.setOnClickListener {
            authenticationViewModel.getCurrentUser()
        }

        binding.userFullNameTV.text = authenticationViewModel.getCurrentUserFullName()
        binding.usernameTV.text = "@${authenticationViewModel.getCurrentUserUsername()}"
        binding.profilePictureCIV.loadProfilePhoto(authenticationViewModel.userId.value)
    }

    private fun initButtons() {
        binding.bottomNavigationBarCL.profileBtn.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary_orange
            )
        )

        binding.bottomNavigationBarCL.homeBtn.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToFeedFragment())
        }

        binding.bottomNavigationBarCL.addNewRecipeBtn.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToCreateRecipeNavigation())
        }

        binding.bottomNavigationBarCL.searchBtn.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionGlobalFragmentSearch3())
        }

        binding.bottomNavigationBarCL.recommendedBtn.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionGlobalRecommendedFragment())
        }

        binding.logOutBtn.setOnClickListener {
            authenticationViewModel.logOutUser()
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAuthenticationFragment())
        }

        binding.editProfileBtn.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment())
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToFeedFragment())
        }

        binding.postedTV.setOnClickListener {
            recipeViewModel.getPublishedRecipeList(authenticationViewModel.userId.value!!)
        }

        binding.cookedTV.setOnClickListener {
            recipeViewModel.getCookedRecipeList()
        }

        binding.savedTV.setOnClickListener {
            recipeViewModel.getSavedRecipeList()
        }


    }

    private fun initObservers() {
        authenticationViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError != null) {
                logTag("isErrorValue", isError.toString())
                showError(isError, requireActivity())
            }
        }

        authenticationViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.isLoadingCIP.isVisible = isLoading
            binding.editProfileBtn.isEnabled = !isLoading
            binding.logOutBtn.isEnabled = !isLoading
            logTag("isLoadingValue", isLoading.toString())
        }

        authenticationViewModel.userData.observe(viewLifecycleOwner) { user ->
            if (authenticationViewModel.getCurrentUserFullName()
                    .isNullOrEmpty() || authenticationViewModel.getCurrentUserUsername()
                    .isNullOrEmpty()
            ) {
                binding.userFullNameTV.text = user!!.fullName
                binding.usernameTV.text = "@${user.username}"
                recipeViewModel.getPublishedRecipeList(authenticationViewModel.userId.value!!)
            }
        }

        recipeViewModel.publishedRecipesForUser.observe(viewLifecycleOwner) { recipeList ->
            logTag("recipelist", recipeList.toString())
            if (recipeList != null) {
                binding.postedNumberTV.text = recipeList.size.toString()
                initRecyclerView(
                    GridLayoutManager(requireContext(), GALLERY_SPAN_COUNT),
                    recipeList.toList() as List<Recipe>
                )
                binding.postedNumberTV.text = recipeList.size.toString()
            }

        }

        recipeViewModel.cookedRecipesList.observe(viewLifecycleOwner) { recipeList ->
            logTag("recipelist", recipeList.toString())
            if (recipeList != null) {
                profileListAdapter.submitList(recipeList)
                profileListAdapter.notifyDataSetChanged()
                binding.cookedNumberTV.text = recipeList.size.toString()
            } else {
                showError("No recipes in Cooked", requireActivity())
            }
        }

        recipeViewModel.savedRecipeList.observe(viewLifecycleOwner) { recipeList ->
            logTag("recipelist", recipeList.toString())
            if (recipeList != null) {
                profileListAdapter.submitList(recipeList)
                profileListAdapter.notifyDataSetChanged()
                binding.savedNumberTV.text = recipeList.size.toString()
            } else {
                showError("No recipes in Saved", requireActivity())
            }
        }

        recipeViewModel.cookedRecipesSize.observe(viewLifecycleOwner) { size ->
            binding.cookedNumberTV.text = size.toString()
        }

        recipeViewModel.savedRecipesSize.observe(viewLifecycleOwner) { size ->
            binding.savedNumberTV.text = size.toString()
        }
    }

    private fun initRecyclerView(
        layoutManager: GridLayoutManager,
        recipeList: List<Recipe>
    ) {
        profileListAdapter = ProfileCollectionAdapter(::onItemClickListener)
        binding.collectionRV.adapter = profileListAdapter
        profileListAdapter.submitList(recipeList)
        binding.collectionRV.layoutManager = layoutManager
    }

    private fun onItemClickListener(recipe: Recipe) {
        findNavController().navigate(
            ProfileFragmentDirections.actionGlobalFragmentViewFullRecipe(
                recipe
            )
        )
    }

    companion object {
        const val GALLERY_SPAN_COUNT = 3
    }
}