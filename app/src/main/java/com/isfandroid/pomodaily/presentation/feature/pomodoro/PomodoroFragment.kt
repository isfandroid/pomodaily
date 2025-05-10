package com.isfandroid.pomodaily.presentation.feature.pomodoro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.FragmentPomodoroBinding
import com.isfandroid.pomodaily.presentation.feature.main.Tasks
import com.isfandroid.pomodaily.presentation.resource.UiState
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_IDLE
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_PAUSED
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_RUNNING
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_BREAK
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_POMODORO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PomodoroFragment: Fragment() {

    private var _binding: FragmentPomodoroBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<PomodoroViewModel>()

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
        observeData()
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

            // Timer Action
            btnRestart.setOnClickListener { viewModel.restartTimer() }
            btnStart.setOnClickListener { viewModel.startTimer() }
            btnPause.setOnClickListener { viewModel.pauseTimer() }
            btnResume.setOnClickListener { viewModel.resumeTimer() }
            btnSkip.setOnClickListener { viewModel.skipForward() }

            // Active Task
            cardActiveTask.setOnClickListener {
                // TODO: Navigate to Schedule
            }
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.timerState.collectLatest {
                        with(binding) {
                            when (it) {
                                TIMER_STATE_IDLE -> {
                                    btnRestart.visibility = View.INVISIBLE
                                    btnSkip.visibility = View.INVISIBLE
                                    btnStart.visibility = View.VISIBLE
                                    btnPause.visibility = View.GONE
                                    btnResume.visibility = View.GONE
                                }
                                TIMER_STATE_RUNNING -> {
                                    btnRestart.visibility = View.VISIBLE
                                    btnSkip.visibility = View.VISIBLE
                                    btnStart.visibility = View.INVISIBLE
                                    btnPause.visibility = View.VISIBLE
                                    btnResume.visibility = View.GONE
                                }
                                TIMER_STATE_PAUSED -> {
                                    btnRestart.visibility = View.VISIBLE
                                    btnSkip.visibility = View.VISIBLE
                                    btnStart.visibility = View.INVISIBLE
                                    btnPause.visibility = View.GONE
                                    btnResume.visibility = View.VISIBLE
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.timerType.collectLatest {
                        val message = when (it) {
                            TIMER_TYPE_POMODORO -> {
                                getString(
                                    R.string.txt_value_stay_focus_for_minutes,
                                    viewModel.pomodoroDurationMinutes.value
                                )
                            }
                            TIMER_TYPE_BREAK -> {
                                getString(
                                    R.string.txt_value_take_a_break_for_minutes,
                                    viewModel.breakDurationMinutes.value
                                )
                            }
                            else -> {
                                getString(
                                    R.string.txt_value_take_a_long_break_for_minutes,
                                    viewModel.longBreakDurationMinutes.value
                                )
                            }
                        }
                        binding.tvMessage.text = message
                    }
                }

                launch {
                    viewModel.remainingTimeSeconds.collectLatest {
                        val minutes = it / 60
                        val seconds = it % 60
                        binding.tvTimer.text = getString(R.string.txt_value_timer, minutes, seconds)
                    }
                }

                launch {
                    viewModel.activeTask.collectLatest {
                        with(binding) {
                            when(it) {
                                is UiState.Loading -> {}
                                is UiState.Error -> {}
                                is UiState.Success -> {
                                    if (it.data == null) {
                                        tvNoActiveTask.visibility = View.VISIBLE
                                        tvActiveTaskName.visibility = View.GONE
                                        tvActiveTaskSessions.visibility = View.GONE
                                        tvActiveTaskTotalMinutes.visibility = View.GONE
                                    } else {
                                        tvNoActiveTask.visibility = View.GONE
                                        tvActiveTaskName.visibility = View.VISIBLE
                                        tvActiveTaskSessions.visibility = View.VISIBLE
                                        tvActiveTaskTotalMinutes.visibility = View.VISIBLE

                                        tvActiveTaskName.text = it.data.name
                                        tvActiveTaskSessions.text = getString(
                                            R.string.txt_value_task_sessions,
                                            it.data.completedSessions,
                                            it.data.pomodoroSessions
                                        )
                                        val totalTaskDuration = it.data.pomodoroSessions * viewModel.pomodoroDurationMinutes.value
                                        tvActiveTaskTotalMinutes.text = getString(R.string.txt_value_minutes, totalTaskDuration)
                                    }
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.pomodoroCount.collectLatest {
                        println("INGPO PERHITUNGAN OM : $it")
                    }
                }
            }
        }
    }
}