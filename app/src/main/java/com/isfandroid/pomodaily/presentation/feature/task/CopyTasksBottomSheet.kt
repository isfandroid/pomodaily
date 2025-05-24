package com.isfandroid.pomodaily.presentation.feature.task

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.color.MaterialColors
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.DialogCopyTaskBinding

class CopyTasksBottomSheet(
    private val days: List<Map<String, Any>>,
    private val onSubmit: (Int) -> Unit,
): BottomSheetDialogFragment() {

    companion object {
        const val TAG = "CopyTasksBottomSheet"
    }

    private var _binding: DialogCopyTaskBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCopyTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {
        with(binding) {
            ivClose.setOnClickListener {
                dismiss()
            }

            days.forEachIndexed { index, data ->
                val radioButton = RadioButton(context)
                radioButton.id = data["id"] as Int
                radioButton.text = data["name"] as String
                radioButton.isChecked = index == 0
                radioButton.setTextAppearance(R.style.Text_Regular_14)

                val checkedColor = MaterialColors.getColor(binding.root, androidx.appcompat.R.attr.colorPrimary)
                val uncheckedColor = MaterialColors.getColor(binding.root, com.google.android.material.R.attr.colorOnBackground)
                val states = arrayOf(
                    intArrayOf(android.R.attr.state_checked),
                    intArrayOf(-android.R.attr.state_checked)
                )
                val colors = intArrayOf(
                    checkedColor,
                    uncheckedColor
                )
                radioButton.buttonTintList = ColorStateList(states, colors)

                rgDays.addView(radioButton)
            }

            btnSubmit.setOnClickListener {
                onSubmit.invoke(rgDays.checkedRadioButtonId)
                dismiss()
            }
        }
    }
}