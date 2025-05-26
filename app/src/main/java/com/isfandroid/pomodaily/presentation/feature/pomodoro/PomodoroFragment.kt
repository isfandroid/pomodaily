package com.isfandroid.pomodaily.presentation.feature.pomodoro

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
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
import com.isfandroid.pomodaily.presentation.feature.main.openAppSettings
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_IDLE
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_PAUSED
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_RUNNING
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_POMODORO
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PomodoroFragment: Fragment() {

    private var _binding: FragmentPomodoroBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<PomodoroViewModel>()

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        binding.layoutMessage.root.isVisible = !isGranted
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        initViews()
        observeData()
    }

    override fun onResume() {
        super.onResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initViews() {
        with(binding) {
            // Notification Permission Message
            layoutMessage.apply {
                ivIllustration.setImageResource(R.drawable.img_illustration_notification)
                tvTitle.text = getString(R.string.txt_msg_enable_notification)
                tvDesc.text = getString(R.string.txt_msg_enable_notification_desc)
                btnSecondaryAction.visibility = View.GONE
                btnAction.text = getString(R.string.txt_grant_permission)
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
                            if (it == null) {
                                tvNoActiveTask.visibility = View.VISIBLE
                                tvActiveTaskName.visibility = View.GONE
                                tvActiveTaskSessions.visibility = View.GONE
                                tvActiveTaskRemainingTime.visibility = View.GONE
                            } else {
                                tvNoActiveTask.visibility = View.GONE
                                tvActiveTaskName.visibility = View.VISIBLE
                                tvActiveTaskSessions.visibility = View.VISIBLE
                                tvActiveTaskRemainingTime.visibility = View.VISIBLE

                                tvActiveTaskName.text = it.name
                                tvActiveTaskSessions.text = getString(
                                    R.string.txt_value_task_sessions,
                                    it.completedSessions,
                                    it.pomodoroSessions
                                )
                                val remainingMinutes = (it.pomodoroSessions - it.completedSessions) * viewModel.pomodoroDuration.value
                                tvActiveTaskRemainingTime.text = getString(R.string.txt_value_minutes_left, remainingMinutes)
                            }
                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkNotificationPermission() {
        val permission = Manifest.permission.POST_NOTIFICATIONS
        val isGranted = ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
        val isPermanentlyDeclined = !shouldShowRequestPermissionRationale(permission)

        if (!isGranted) {
            binding.layoutMessage.root.visibility = View.VISIBLE
            binding.layoutMessage.btnAction.setOnClickListener {
                if (isPermanentlyDeclined) {
                    requireActivity().openAppSettings()
                } else {
                    requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            binding.layoutMessage.root.visibility = View.GONE
        }
    }
}