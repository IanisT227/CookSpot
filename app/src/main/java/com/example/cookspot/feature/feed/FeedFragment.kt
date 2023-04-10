package com.example.cookspot.feature.feed

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentFeedBinding
import com.example.cookspot.feature.authentication.AuthenticationViewModel
import com.example.cookspot.logTag
import com.example.cookspot.showAlerter
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FeedFragment : Fragment(R.layout.fragment_feed) {
    private val binding by viewBinding(FragmentFeedBinding::bind)
    private val authenticationViewModel: AuthenticationViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initViews()
        authenticationViewModel.getCurrentUserId()
        binding.logOutBtn.setOnClickListener {
            authenticationViewModel.logOutUser()
            findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToAuthenticationFragment())
        }
    }

    private fun initViews() {
        binding.homeBtn.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary_orange
            )
        )
    }

    private fun initObservers() {
        authenticationViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            logTag("isLoadingValue", isLoading.toString())
        }

        authenticationViewModel.isError.observe(viewLifecycleOwner) { isError ->
            if (isError != null) {
                logTag("isErrorValue", isError.toString())
                showAlerter(isError, requireActivity())
            }
        }

        authenticationViewModel.userId.observe(viewLifecycleOwner) { userId ->
            binding.usernameTV.text = userId
        }
    }
}