package com.isfandroid.pomodaily.presentation.feature.pomodoro

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.FragmentPomodoroBinding
import com.isfandroid.pomodaily.presentation.feature.main.Schedule
import com.isfandroid.pomodaily.presentation.feature.main.Settings
import com.isfandroid.pomodaily.presentation.feature.main.Statistics
import com.isfandroid.pomodaily.presentation.feature.main.Tasks
import com.isfandroid.pomodaily.presentation.resource.UiState
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_IDLE
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_PAUSED
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_RUNNING
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_POMODORO
import com.isfandroid.pomodaily.utils.Helper.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PomodoroFragment: Fragment() {

    private var _binding: FragmentPomodoroBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<PomodoroViewModel>()

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) {
            showSnackbar(
                view = binding.root,
                message = getString(R.string.txt_msg_no_timer_notification_from_app),
                isError = true
            )
        }
    }

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
            // Notification Permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

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
                findNavController().navigate(
                    route = Statistics,
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
            btnSettings.setOnClickListener {
                findNavController().navigate(
                    route = Settings,
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

            // Timer Action
            btnRestart.setOnClickListener {
                val intent = PomodoroService.createControlIntent(requireContext(), PomodoroService.ACTION_RESTART)
                requireContext().startService(intent)
            }
            btnStart.setOnClickListener {
                val intent = PomodoroService.createControlIntent(requireContext(), PomodoroService.ACTION_START)
                requireContext().startService(intent)
            }
            btnPause.setOnClickListener {
                val intent = PomodoroService.createControlIntent(requireContext(), PomodoroService.ACTION_PAUSE)
                requireContext().startService(intent)
            }
            btnResume.setOnClickListener {
                val intent = PomodoroService.createControlIntent(requireContext(), PomodoroService.ACTION_RESUME)
                requireContext().startService(intent)
            }
            btnSkip.setOnClickListener {
                val intent = PomodoroService.createControlIntent(requireContext(), PomodoroService.ACTION_SKIP)
                requireContext().startService(intent)
            }

            // Active Task
            cardActiveTask.setOnClickListener {
                findNavController().navigate(
                    route = Schedule,
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
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.timerData.collectLatest {
                        with(binding) {
                            // Timer State
                            when (it.state) {
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

                            // Timer Type
                            val message = when (it.type) {
                                TIMER_TYPE_POMODORO -> getString(R.string.txt_stay_focus)
                                else -> getString(R.string.txt_take_a_break)
                            }
                            binding.tvMessage.text = message

                            // Timer Remaining Time
                            val minutes = it.remainingTime / 60
                            val seconds = it.remainingTime % 60
                            binding.tvTimer.text = getString(R.string.txt_value_timer, minutes, seconds)
                        }
                    }
                }

                launch {
                    viewModel.activeTask.collectLatest {
                        with(binding) {
                            if (it is UiState.Success) {
                                if (it.data == null) {
                                    tvNoActiveTask.visibility = View.VISIBLE
                                    tvActiveTaskName.visibility = View.GONE
                                    tvActiveTaskSessions.visibility = View.GONE
                                    tvActiveTaskRemainingTime.visibility = View.GONE
                                } else {
                                    tvNoActiveTask.visibility = View.GONE
                                    tvActiveTaskName.visibility = View.VISIBLE
                                    tvActiveTaskSessions.visibility = View.VISIBLE
                                    tvActiveTaskRemainingTime.visibility = View.VISIBLE

                                    tvActiveTaskName.text = it.data.name
                                    tvActiveTaskSessions.text = getString(
                                        R.string.txt_value_task_sessions,
                                        it.data.completedSessions,
                                        it.data.pomodoroSessions
                                    )
                                    val remainingMinutes = (it.data.pomodoroSessions - it.data.completedSessions) * viewModel.pomodoroDuration.value
                                    tvActiveTaskRemainingTime.text = getString(R.string.txt_value_minutes_left, remainingMinutes)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}