package com.example.cookspot.feature.profile

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookspot.R
import com.example.cookspot.databinding.BottomNavigationLayoutBinding
import com.example.cookspot.databinding.FragmentProfileBinding
import com.example.cookspot.feature.authentication.AuthenticationFragment
import com.example.cookspot.feature.feed.FeedFragmentDirections
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private val binding by viewBinding(FragmentProfileBinding::bind)
    private var doubleBackPressed = false
    private var bottomNavigationBarBinding: BottomNavigationLayoutBinding? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initButtons()
    }

    private fun initViews() {
        bottomNavigationBarBinding = binding.bottomNavigationBarCL

        bottomNavigationBarBinding!!.profileBtn.setColorFilter(
            ContextCompat.getColor(
                requireContext(),
                R.color.primary_orange
            )
        )
    }

    private fun initButtons() {

        binding.bottomNavigationBarCL.homeBtn.setOnClickListener {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToFeedFragment())
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToFeedFragment())
        }
    }

}