package com.isfandroid.pomodaily.presentation.feature.task

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
                radioButton.highlightColor = MaterialColors.getColor(this.root, com.google.android.material.R.attr.colorPrimary)
                rgDays.addView(radioButton)
            }

            btnSubmit.setOnClickListener {
                onSubmit.invoke(rgDays.checkedRadioButtonId)
                dismiss()
            }
        }
    }
}