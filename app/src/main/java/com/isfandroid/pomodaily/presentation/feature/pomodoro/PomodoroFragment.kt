package com.isfandroid.pomodaily.presentation.feature.pomodoro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.FragmentPomodoroBinding
import com.isfandroid.pomodaily.presentation.feature.Tasks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PomodoroFragment: Fragment() {

    private var _binding: FragmentPomodoroBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPomodoroBinding.inflate(inflater, container, false)
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
            // Menu
            btnTasks.setOnClickListener {
                findNavController().navigate(
                    route = Tasks,
                    navOptions = navOptions {
                        anim {
                            enter = R.anim.slide_in_right
                            exit = R.anim.slide_out_left
                            popEnter = R.anim.slide_in_left
                            popExit = R.anim.slide_out_right
                        }
                    },
                )
            }
            btnStats.setOnClickListener {
                // TODO: Navigate to Stats
            }
            btnSettings.setOnClickListener {
                // TODO: Navigate to Settings
            }
        }
    }
}