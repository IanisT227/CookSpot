package com.example.cookspot.feature.create_new_recipe

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.awesomedialog.AwesomeDialog
import com.example.awesomedialog.body
import com.example.awesomedialog.onNegative
import com.example.awesomedialog.onPositive
import com.example.awesomedialog.title
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentCreateNewRecipeStepThreeBinding
import com.example.cookspot.showError
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class CreateRecipeStepThreeFragment : Fragment(R.layout.fragment_create_new_recipe_step_three) {

    private val binding by viewBinding(FragmentCreateNewRecipeStepThreeBinding::bind)
    private val args: CreateRecipeStepThreeFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initButtons()
    }

    private fun initViews() {

        binding.ingredientsTIEE.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(e: Editable) {}
            override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {}
            override fun onTextChanged(
                text: CharSequence,
                start: Int,
                lengthBefore: Int,
                lengthAfter: Int
            ) {
                if (lengthAfter > lengthBefore) {
                    if (text.toString().length == 1) {
                        binding.ingredientsTIEE.setText("• $text")
                        binding.ingredientsTIEE.setSelection(binding.ingredientsTIEE.text!!.length)
                    }
                    if (text.toString().endsWith("\n")) {
                        var newText = text.toString().replace("\n", "\n• ")
                        newText = newText.toString().replace("• •", "•")
                        binding.ingredientsTIEE.setText(newText)
                        binding.ingredientsTIEE.setSelection(binding.ingredientsTIEE.text!!.length)
                    }
                }
            }
        })
    }

    private fun initButtons() {
        binding.nextStepBtn.setOnClickListener {
            if (checkFields()) {
                val recipe = args.newRecipe !!.copy(
                    ingredients = binding.ingredientsTIEE.text.toString(),
                    cookingProcess = binding.processTIEE.text.toString(),
                )
                findNavController().navigate(
                    CreateRecipeStepThreeFragmentDirections.actionFragmentCreateRecipeStepThreeToRecipeSummaryFragment(
                        finalRecipe = recipe
                    )
                )
            } else {
                showError("All fields must be completed", requireActivity())
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
                    CreateRecipeStepThreeFragmentDirections.actionGlobalFeedFragment()
                )
            }
            .onNegative(text = "Cancel") {
            }
    }

    private fun checkFields(): Boolean {
        return !(binding.ingredientsTIEE.text.isNullOrEmpty() || binding.processTIEE.text.isNullOrEmpty())
    }
}