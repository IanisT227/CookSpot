package com.example.cookspot.feature.authentication

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.cookspot.R
import com.example.cookspot.checkMail
import com.example.cookspot.checkUserOrPassword
import com.example.cookspot.databinding.FragmentRegisterBinding
import com.example.cookspot.showAlerter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private val binding by viewBinding(FragmentRegisterBinding::bind)
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initFirebase()
        initButtons()

    }

    private fun initFirebase() {
        firebaseAuth = Firebase.auth
    }

    private fun firebaseSignUp(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showAlerter("Registration was successfull", requireActivity())
            } else {
                showAlerter(task.exception?.message.toString(), requireActivity())

            }
        }
    }

    private fun initButtons() {
        binding.goBackRegisterBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            findNavController().navigateUp()
        }

        binding.signUpBtn.setOnClickListener {
            if (!(checkMail(binding.emailTIEE))) {
                showAlerter("Invalid mail format", requireActivity())
            } else {
                if (checkUserOrPassword(binding.usernameTIEE) && checkUserOrPassword(
                        binding.nameTIEE
                    ) && checkUserOrPassword(binding.passwordTIEE)
                ) {
                    showAlerter("All fields must have at least 8 characters", requireActivity())
                } else {
                    firebaseSignUp(
                        binding.emailTIEE.text.toString(),
                        binding.passwordTIEE.text.toString()
                    )
                }
            }

        }
    }
}