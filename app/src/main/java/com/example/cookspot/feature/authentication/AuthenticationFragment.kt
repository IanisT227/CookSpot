package com.example.cookspot.feature.authentication

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentAuthenticationBinding
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class AuthenticationFragment : Fragment(R.layout.fragment_authentication) {

    private val binding by viewBinding(FragmentAuthenticationBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}