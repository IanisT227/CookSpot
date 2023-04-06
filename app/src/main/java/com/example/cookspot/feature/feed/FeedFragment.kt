package com.example.cookspot.feature.feed

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentFeedBinding
import com.example.cookspot.feature.authentication.AuthenticationViewModel
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class FeedFragment : Fragment(R.layout.fragment_feed) {
    private val binding by viewBinding(FragmentFeedBinding::bind)
    private val authenticationViewModel: AuthenticationViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.usernameTV.text = authenticationViewModel.getCurrentUser()
    }
}