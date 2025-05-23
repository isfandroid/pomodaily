package com.isfandroid.pomodaily.presentation.feature.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.DialogMessageBinding

class StatsInfoBottomSheet: BottomSheetDialogFragment() {

    companion object {
        const val TAG = "StatsInfoBottomSheet"
    }

    private var _binding: DialogMessageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            ivClose.setOnClickListener { dismiss() }
            tvTitle.text = getString(R.string.txt_how_statistics_works)
            tvMessage.text = getString(R.string.txt_msg_statistics_info)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}