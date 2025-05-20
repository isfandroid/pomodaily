package com.isfandroid.pomodaily.presentation.feature.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.FragmentTasksBinding
import com.isfandroid.pomodaily.presentation.common.adapter.ExpandableTaskAdapter
import com.isfandroid.pomodaily.presentation.common.helper.RecyclerViewLinearItemDecoration
import com.isfandroid.pomodaily.presentation.resource.UiState
import com.isfandroid.pomodaily.utils.Constant.DAYS_OF_WEEK
import com.isfandroid.pomodaily.utils.Helper.showSnackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment: Fragment() {

    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<TaskViewModel>()

    private val taskAdapter by lazy {
        ExpandableTaskAdapter(
            onItemClick = {
                viewModel.deleteNewTaskEntry()
                if (it.isExpanded) {
                    viewModel.setExpandedTaskId(null)
                } else {
                    viewModel.setExpandedTaskId(it.id)
                }
            },
            onDeleteClick = {
                if (it.isNewEntry) {
                    viewModel.deleteNewTaskEntry()
                } else {
                    viewModel.deleteTask(it)
                }
                viewModel.setExpandedTaskId(null)
            },
            onCancelClick = {
                viewModel.deleteNewTaskEntry()
                viewModel.setExpandedTaskId(null)
            },
            onSaveClick = {
                viewModel.deleteNewTaskEntry()
                viewModel.updateTask(it)
                viewModel.setExpandedTaskId(null)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
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
            appBar.tvTitle.text = getString(R.string.txt_tasks)
            appBar.btnBack.setOnClickListener { findNavController().navigateUp() }

            // Chips - Days of Week
            DAYS_OF_WEEK.forEach { day ->
                val dayId = day["id"] as Int
                val dayName = day["name"] as String
                createDaysChip(dayId, dayName)
            }

            // RV - Tasks
            rvItems.layoutManager = LinearLayoutManager(requireContext())
            rvItems.adapter = taskAdapter
            rvItems.addItemDecoration(
                RecyclerViewLinearItemDecoration(
                    topSpace = resources.getDimensionPixelSize(R.dimen.dimen_0),
                    bottomSpace = resources.getDimensionPixelSize(R.dimen.dimen_16),
                    rightSpace = resources.getDimensionPixelSize(R.dimen.dimen_0),
                    leftSpace = resources.getDimensionPixelSize(R.dimen.dimen_0),
                    addBottomSpacingForLastItem = true
                )
            )

            // Buttons
            btnAdd.setOnClickListener {
                viewModel.addNewTaskEntry()
            }
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.selectedDayId.collectLatest {
                        binding.cgDays.check(it)

                        val selectedChip = binding.cgDays.findViewById<Chip>(it)
                        if (selectedChip != null) {
                            binding.svDays.post {
                                val scrollX = selectedChip.left - (binding.svDays.width / 2) + (selectedChip.width / 2)
                                binding.svDays.smoothScrollTo(scrollX, 0)
                            }
                        }
                    }
                }

                launch {
                    viewModel.tasks.collectLatest {
                        with(binding) {
                            when(it) {
                                is UiState.Loading -> {
                                    // TODO: showTaskLoading(true)
                                }
                                is UiState.Error -> {
                                    // TODO: showTaskLoading(false)
                                    rvItems.isVisible = false
                                    layoutError.root.isVisible = true
                                    btnAdd.visibility = View.GONE

                                    layoutError.tvTitle.text = getString(R.string.txt_msg_title_load_tasks_failed)
                                    layoutError.tvDesc.text = getString(R.string.txt_msg_desc_load_tasks_failed)
                                    layoutError.btnAction.text = getString(R.string.txt_retry)
                                    layoutError.btnAction.setOnClickListener {
                                        viewModel.refreshTasks()
                                    }
                                }
                                is UiState.Success -> {
                                    // TODO: showTaskLoading(false)
                                    if (it.data.isNullOrEmpty()) {
                                        rvItems.isVisible = false
                                        layoutError.root.isVisible = true
                                        btnAdd.visibility = View.GONE

                                        val dayName = DAYS_OF_WEEK.first { day ->
                                            day["id"] == viewModel.selectedDayId.value
                                        }["name"] as String
                                        layoutError.ivIllustration.setImageResource(R.drawable.img_illustration_empty)
                                        layoutError.tvTitle.text = getString(R.string.txt_empty_data)
                                        layoutError.tvDesc.text = getString(R.string.txt_value_no_tasks_for_day, dayName)
                                        layoutError.btnAction.text = getString(R.string.txt_add_task)
                                        layoutError.btnAction.setOnClickListener {
                                            viewModel.addNewTaskEntry()
                                        }
                                    }
                                    else {
                                        rvItems.isVisible = true
                                        layoutError.root.isVisible = false
                                        btnAdd.isVisible = !it.data.any { it.isNewEntry || it.isExpanded }

                                        taskAdapter.submitList(it.data)
                                    }
                                }
                            }
                        }
                    }
                }

                launch {
                    viewModel.daysWithTasks.collectLatest { result ->
                        if (result is UiState.Success && !result.data.isNullOrEmpty() && viewModel.tasks.value is UiState.Success && viewModel.tasks.value.data.isNullOrEmpty()) {
                            binding.layoutError.btnSecondaryAction.visibility = View.VISIBLE
                            binding.layoutError.btnSecondaryAction.text = getString(R.string.txt_copy_tasks)
                            binding.layoutError.btnSecondaryAction.setOnClickListener {
                                val dayIds = result.data.orEmpty()
                                val days = DAYS_OF_WEEK.filter { mDays ->
                                    (mDays["id"] as Int) in dayIds
                                }
                                showCopyTasksBottomSheet(days)
                            }
                        } else {
                            binding.layoutError.btnSecondaryAction.visibility = View.GONE
                        }
                    }
                }

                launch {
                    viewModel.updateTaskResult.collectLatest {
                        when {
                            it is UiState.Error -> {
                                showSnackbar(
                                    view = binding.root,
                                    message = getString(R.string.txt_msg_update_tasks_failed),
                                    isError = true
                                )
                            }
                            it is UiState.Success -> {
                                showSnackbar(
                                    view = binding.root,
                                    message = getString(R.string.txt_msg_update_tasks_successful),
                                    isError = false
                                )
                            }
                        }
                    }
                }

                launch {
                    viewModel.deleteTaskResult.collectLatest {
                        when {
                            it is UiState.Error -> {
                                showSnackbar(
                                    view = binding.root,
                                    message = getString(R.string.txt_msg_delete_task_failed),
                                    isError = true
                                )
                            }
                            it is UiState.Success -> {
                                showSnackbar(
                                    view = binding.root,
                                    message = getString(R.string.txt_msg_delete_task_successful),
                                    isError = false
                                )
                            }
                        }
                    }
                }

                launch {
                    viewModel.copyTasksResult.collectLatest {
                        when {
                            it is UiState.Error -> {
                                showSnackbar(
                                    view = binding.root,
                                    message = getString(R.string.txt_msg_copy_tasks_failed),
                                    isError = true
                                )
                            }
                            it is UiState.Success -> {
                                showSnackbar(
                                    view = binding.root,
                                    message = getString(R.string.txt_msg_add_tasks_successful),
                                    isError = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createDaysChip(dayId: Int, dayName: String): Chip {
        val chip = Chip(context)
        chip.id = dayId
        chip.setEnsureMinTouchTargetSize(false)
        chip.text = dayName
        chip.isCheckable = true
        chip.setOnClickListener {
            viewModel.deleteNewTaskEntry()
            viewModel.selectDay(dayId)
        }

        binding.cgDays.addView(chip)
        return chip
    }

    private fun showCopyTasksBottomSheet(days: List<Map<String, Any>>) {
        CopyTasksBottomSheet(
            days = days,
            onSubmit = { selectedDayId ->
                viewModel.copyTasks(selectedDayId, viewModel.selectedDayId.value)
            }
        ).show(childFragmentManager, CopyTasksBottomSheet.TAG)
    }
}