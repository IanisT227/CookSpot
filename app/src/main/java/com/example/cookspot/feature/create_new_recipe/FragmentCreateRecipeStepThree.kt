package com.example.cookspot.feature.create_new_recipe

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.cookspot.R
import com.example.cookspot.databinding.FragmentCreateNewRecipeStepThreeBinding
import com.example.cookspot.model.Recipe
import com.example.cookspot.showAlerter
import com.zhuinden.fragmentviewbindingdelegatekt.viewBinding

class FragmentCreateRecipeStepThree : Fragment(R.layout.fragment_create_new_recipe_step_three) {

    private val binding by viewBinding(FragmentCreateNewRecipeStepThreeBinding::bind)
    private val args: FragmentCreateRecipeStepThreeArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initButtons()
    }

    private fun initViews() {
        binding.ingredientsTIEE.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                val selectionStart = binding.ingredientsTIEE.selectionStart
                binding.ingredientsTIEE.selectionEnd
                val editable = binding.ingredientsTIEE.text
                val start = binding.ingredientsTIEE.layout.getLineStart(
                    binding.ingredientsTIEE.layout.getLineForOffset(selectionStart)
                )
                val end = binding.ingredientsTIEE.layout.getLineEnd(
                    binding.ingredientsTIEE.layout.getLineForOffset(selectionStart)
                )
                val lineText = editable?.subSequence(start, end).toString()

                if (lineText.trim { it <= ' ' }.isEmpty()) {
                    editable?.insert(start, "• ")
                } else {
                    editable?.insert(end, "\n• ")
                }
                return@OnKeyListener true
            }
            false
        })
    }

    private fun initButtons() {
        binding.nextStepBtn.setOnClickListener {
            if (checkFields()) {
                val recipe: Recipe = Recipe(
                    name = args.newRecipe!!.name,
                    duration = args.newRecipe!!.duration,
                    imageUri = args.newRecipe!!.imageUri,
                    makes = args.newRecipe!!.makes,
                    args.newRecipe!!.difficulty,
                    args.newRecipe!!.tags,
                    binding.ingredientsTIEE.text.toString(),
                    binding.processTIEE.text.toString(),
                    args.newRecipe!!.publisherId
                )
                findNavController().navigate(
                    FragmentCreateRecipeStepThreeDirections.actionFragmentCreateRecipeStepThreeToRecipeDetailFragment(
                        finalRecipe = recipe
                    )
                )
            } else {
                showAlerter("All fields must be completed", requireActivity())
            }
        }
    }

    private fun checkFields(): Boolean {
        return !(binding.ingredientsTIEE.text.isNullOrEmpty() || binding.processTIEE.text.isNullOrEmpty())
    }
}