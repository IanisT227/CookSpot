package com.example.cookspot.feature.create_new_recipe

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.body
import com.example.awesomedialog.onNegative
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.title
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentCreateNewRecipeStepTwoBinding
import com.example.cookspot.logTag
import com.example.cookspot.showError
import com.example.cookspot.viewmodel.RecipeViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FragmentCreateRecipeStepTwo : Fragment(R.layout.fragment_create_new_recipe_step_two) {

    private val recipeViewModel: RecipeViewModel by activityViewModel()
    private val binding by viewBinding(FragmentCreateNewRecipeStepTwoBinding::bind)
    private val args: FragmentCreateRecipeStepTwoArgs by navArgs()
    private var difficultyLevel: String? = null
    private var tagItemsList: ArrayList<String> = ArrayList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.v("arguments", args.newRecipe.toString())
        initButtons()
        initObservers()
        recipeViewModel.initFirebase()
        recipeViewModel.getTags()
    }

    private fun initRecyclerView(layoutManager: GridLayoutManager, tagList: List<String>) {
        val tagListAdapter = TagListAdapter(::onItemClickListener)
        binding.tagsRV.adapter = tagListAdapter
        tagListAdapter.submitList(tagList)
        binding.tagsRV.layoutManager = layoutManager
    }

    private fun onItemClickListener(tag: String): Boolean {
        return if (tag !in tagItemsList) {
            if (tagItemsList.size < 3) {
                tagItemsList.add(tag)
                false
            } else {
                showError("Remove a tag before adding another", requireActivity())
                true
            }
        } else {
            tagItemsList.remove(tag)
            false
        }
        logTag("itemlisttag", tagItemsList.toString())
    }

    private fun initButtons() {
        binding.radioEasyBtn.setOnClickListener {
            difficultyLevel = "Easy"
        }

        binding.radioMediumBtn.setOnClickListener {
            difficultyLevel = "Medium"
        }

        binding.radioHardBtn.setOnClickListener {
            difficultyLevel = "Hard"
        }

        binding.nextStepBtn.setOnClickListener {
            if (checkFields()) {
                showError("Please choose a difficulty level and three tags", requireActivity())
            } else {
                val recipe = args.newRecipe !!.copy(
                    tags = tagItemsList,
                    makes = args.newRecipe !!.makes,
                    difficulty = difficultyLevel !!,
                    publisherId = args.newRecipe !!.publisherId,
                )
                findNavController().navigate(
                    FragmentCreateRecipeStepTwoDirections.actionFragmentCreateRecipeStepTwoToFragmentCreateRecipeStepThree(
                        recipe
                    )
                )
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            showDialog()
        }

        binding.toolbar.setNavigationOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        AwesomeDialog.build(requireActivity())
            .title(title = "Discard recipe?", titleColor = R.color.neutral_black)
            .body(
                body = "If you go back, your progress will be deleted",
                color = R.color.neutral_black
            )
            .onPositive(text = "Discard") {
                findNavController().navigate(
                    FragmentCreateRecipeStepTwoDirections.actionGlobalFeedFragment()
                )
            }
            .onNegative(text = "Cancel") {
            }
    }

    private fun initObservers() {
        recipeViewModel.recipeTags.observe(viewLifecycleOwner) { tagList ->
            if (tagList != null)
                initRecyclerView(
                    GridLayoutManager(requireContext(), TAG_SPAN_COUNT),
                    tagList.keys.toList()
                )
        }

        recipeViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            logTag("isLoadingValue", isLoading.toString())
            binding.isLoadingCIP.isVisible = isLoading
            binding.nextStepBtn.isEnabled = ! isLoading
        }

        recipeViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError != null) {
                logTag("isErrorValue", isError.toString())
                showError(isError, requireActivity())
            }
        }
    }

    private fun checkFields(): Boolean {
        return (difficultyLevel == null || tagItemsList.size != 3)
    }

    companion object {
        const val TAG_SPAN_COUNT = 3
    }
}