package com.isfandroid.pomodaily.presentation.feature.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.FragmentOnBoardingBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment(
    private val type: Int
): Fragment() {

    private var _binding: FragmentOnBoardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
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
            when (type) {
                1 -> {
                    ivIllustration.setImageResource(R.drawable.img_illustration_create_tasks)
                    tvTitle.text = getString(R.string.txt_title_on_boarding_1)
                    tvDesc.text = getString(R.string.txt_desc_on_boarding_1)
                }
                2 -> {
                    ivIllustration.setImageResource(R.drawable.img_illustration_store_data)
                    tvTitle.text = getString(R.string.txt_title_on_boarding_2)
                    tvDesc.text = getString(R.string.txt_desc_on_boarding_2)
                }
                3 -> {
                    ivIllustration.setImageResource(R.drawable.img_app_logo)
                    tvTitle.text = getString(R.string.txt_title_on_boarding_3)
                    tvDesc.text = getString(R.string.txt_desc_on_boarding_3)
                }
            }
        }
    }
}