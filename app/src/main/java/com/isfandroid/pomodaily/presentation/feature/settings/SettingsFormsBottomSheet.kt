package com.isfandroid.pomodaily.presentation.feature.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.DialogSettingsFormsBinding

class SettingsFormsBottomSheet (
    private val title: String,
    private val formTitle: String,
    private val showNumberForm: Boolean = false,
    private var numberFormValue: Int? = null,
    private val showMultipleChoicesForm: Boolean = false,
    private val multipleChoicesFormValue: String? = null,
    private val multipleChoicesItems: Array<String>? = null,
    private val onSubmit: (Any) -> Unit
): BottomSheetDialogFragment() {

    companion object {
        val TAG = BottomSheetDialogFragment::class.java.simpleName
    }

    private var _binding: DialogSettingsFormsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogSettingsFormsBinding.inflate(inflater, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
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
            tvTitle.text = title
            ivClose.setOnClickListener { dismiss() }

            if (showNumberForm) {
                tvFormTitleNumber.visibility = View.VISIBLE
                tilNumber.visibility = View.VISIBLE
                tvFormMultipleChoices.visibility = View.GONE
                tilMultipleChoices.visibility = View.GONE
                tvFormTitleNumber.text = formTitle

                etNumber.setText(numberFormValue.toString())
                etNumber.doOnTextChanged { _, _, _, _ ->
                    tilNumber.error = ""
                    numberFormValue = if (etNumber.text.toString().isNotEmpty()) {
                        etNumber.text.toString().toInt()
                    } else {
                        0
                    }
                }
                btnIncrement.setOnClickListener {
                    numberFormValue = numberFormValue!! + 1
                    etNumber.setText(numberFormValue.toString())
                }
                btnDecrement.setOnClickListener {
                    numberFormValue = numberFormValue!! - 1
                    etNumber.setText(numberFormValue.toString())
                }

                btnSubmit.setOnClickListener {
                    val updatedValue = etNumber.text.toString().trim()
                    if (updatedValue.isEmpty() || updatedValue == "0") {
                        tilNumber.error = getString(R.string.txt_msg_field_required_cant_be_0)
                    } else {
                        onSubmit.invoke(numberFormValue as Int)
                        dismiss()
                    }
                }
            }

            if (showMultipleChoicesForm) {
                tvFormMultipleChoices.visibility = View.VISIBLE
                tilMultipleChoices.visibility = View.VISIBLE
                tvFormTitleNumber.visibility = View.INVISIBLE
                tilNumber.visibility = View.INVISIBLE
                tvFormMultipleChoices.text = formTitle

                (tilMultipleChoices.editText as MaterialAutoCompleteTextView).setSimpleItems(multipleChoicesItems.orEmpty())
                acMultipleChoices.setText(multipleChoicesFormValue)

                btnSubmit.setOnClickListener {
                    onSubmit.invoke(acMultipleChoices.text)
                    dismiss()
                }
            }

        }
    }
}