package com.example.cookspot.feature.search

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentSearchBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class FragmentSearch : Fragment(R.layout.fragment_search) {
    private val binding by viewBinding(FragmentSearchBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
    }

    private fun initViews() {
        binding.bottomNavigationBarCL.homeBtn.setOnClickListener {
            findNavController().navigate(
                FragmentSearchDirections.actionFragmentSearchToFeedFragment()
            )
        }

        binding.bottomNavigationBarCL.addNewRecipeBtn.setOnClickListener {
            findNavController().navigate(
                FragmentSearchDirections.actionFragmentSearchToCreateRecipeNavigation()
            )
        }

        binding.bottomNavigationBarCL.profileBtn.setOnClickListener {
            findNavController().navigate(
                FragmentSearchDirections.actionFragmentSearchToUserProfile()
            )
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(FragmentSearchDirections.actionFragmentSearchToFeedFragment())
        }

        binding.searchTIET.hint = "Search..."

        binding.searchTIET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                if (text.toString().isNotEmpty()) {
                    binding.searchTIET.hint = ""
                } else {
                    binding.searchTIET.hint = "Search..."
                }
            }
        })


    }

    private fun initObservables() {

    }
}