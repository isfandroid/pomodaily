package com.isfandroid.pomodaily.presentation.feature.stats

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
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.FragmentStatsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs

@AndroidEntryPoint
class StatsFragment: Fragment() {

    private var _binding: FragmentStatsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<StatsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatsBinding.inflate(inflater, container, false)
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
            // App Bar
            appBar.tvTitle.text = getString(R.string.txt_statistics)
            appBar.btnBack.setOnClickListener { findNavController().navigateUp() }
            appBar.btnAction.visibility = View.VISIBLE
            appBar.btnAction.setImageResource(R.drawable.ic_info_24_on_background)
            appBar.btnAction.setOnClickListener {
                StatsInfoBottomSheet().show(childFragmentManager, StatsInfoBottomSheet.TAG)
            }

            // Stats
            statsToday.tvTitle.text = getString(R.string.txt_today)
            statsThisWeek.tvTitle.text = getString(R.string.txt_this_week)
            statsThisMonth.tvTitle.text = getString(R.string.txt_this_month)
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.todayStats.collectLatest {
                        it ?: return@collectLatest

                        with(binding.statsToday) {
                            tvTasksCount.text = if (it.totalTasks != 0) {
                                getString(R.string.txt_value_stats_tasks_done, "${it.completedTasks} / ${it.totalTasks}")
                            } else {
                                getString(R.string.txt_no_tasks)
                            }

                            tvPreviousTasksCount.text = if (it.previousTotalTasks != 0) {
                                getString(R.string.txt_value_stats_yesterday, "${it.previousCompletedTasks} / ${it.previousTotalTasks}")
                            } else {
                                getString(R.string.txt_value_stats_yesterday, "-")
                            }

                            if (it.totalTasks != 0 && it.previousTotalTasks != 0 && it.completionRate > 0 && it.previousCompletionRate > 0 && it.completionRate != it.previousCompletionRate) {
                                tvDifference.visibility = View.VISIBLE
                                tvDifference.text = getString(R.string.txt_value_percentage, abs(it.completionRate - it.previousCompletionRate))
                                tvDifference.setCompoundDrawablesWithIntrinsicBounds(
                                    if (it.completionRate > it.previousCompletionRate) R.drawable.ic_arrow_up_16_on_surface else R.drawable.ic_arrow_up_16_on_surface.rotateRight(90),
                                    0,
                                    0,
                                    0
                                )
                            } else {
                                tvDifference.visibility = View.GONE
                            }
                        }
                    }
                }

                launch {
                    viewModel.thisWeekStats.collectLatest {
                        it ?: return@collectLatest

                        with(binding.statsThisWeek) {
                            tvTasksCount.text = if (it.totalTasks != 0) {
                                getString(R.string.txt_value_stats_tasks_done, "${it.completedTasks} / ${it.totalTasks}")
                            } else {
                                getString(R.string.txt_no_tasks)
                            }

                            tvPreviousTasksCount.text = if (it.previousTotalTasks != 0) {
                                getString(R.string.txt_value_stats_last_week, "${it.previousCompletedTasks} / ${it.previousTotalTasks}")
                            } else {
                                getString(R.string.txt_value_stats_last_week, "-")
                            }

                            if (it.totalTasks != 0 && it.previousTotalTasks != 0 && it.completionRate > 0 && it.previousCompletionRate > 0 && it.completionRate != it.previousCompletionRate) {
                                tvDifference.visibility = View.VISIBLE
                                tvDifference.text = getString(R.string.txt_value_percentage, abs(it.completionRate - it.previousCompletionRate))
                                tvDifference.setCompoundDrawablesWithIntrinsicBounds(
                                    if (it.completionRate > it.previousCompletionRate) R.drawable.ic_arrow_up_16_on_surface else R.drawable.ic_arrow_up_16_on_surface.rotateRight(90),
                                    0,
                                    0,
                                    0
                                )
                            } else {
                                tvDifference.visibility = View.GONE
                            }
                        }
                    }
                }

                launch {
                    viewModel.thisMonthStats.collectLatest {
                        it ?: return@collectLatest

                        with(binding.statsThisMonth) {
                            tvTasksCount.text = if (it.totalTasks != 0) {
                                getString(R.string.txt_value_stats_tasks_done, "${it.completedTasks} / ${it.totalTasks}")
                            } else {
                                getString(R.string.txt_no_tasks)
                            }

                            tvPreviousTasksCount.text = if (it.previousTotalTasks != 0) {
                                getString(R.string.txt_value_stats_last_month, "${it.previousCompletedTasks} / ${it.previousTotalTasks}")
                            } else {
                                getString(R.string.txt_value_stats_last_month, "-")
                            }

                            if (it.totalTasks != 0 && it.previousTotalTasks != 0 && it.completionRate > 0 && it.previousCompletionRate > 0 && it.completionRate != it.previousCompletionRate) {
                                tvDifference.visibility = View.VISIBLE
                                tvDifference.text = getString(R.string.txt_value_percentage, abs(it.completionRate - it.previousCompletionRate))
                                tvDifference.setCompoundDrawablesWithIntrinsicBounds(
                                    if (it.completionRate > it.previousCompletionRate) R.drawable.ic_arrow_up_16_on_surface else R.drawable.ic_arrow_up_16_on_surface.rotateRight(90),
                                    0,
                                    0,
                                    0
                                )
                            } else {
                                tvDifference.visibility = View.GONE
                            }
                        }
                    }
                }

            }
        }
    }
}