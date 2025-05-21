package com.isfandroid.pomodaily.presentation.feature.schedule

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
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.FragmentScheduleBinding
import com.isfandroid.pomodaily.presentation.common.adapter.TaskScheduleAdapter
import com.isfandroid.pomodaily.presentation.common.helper.RecyclerViewLinearItemDecoration
import com.isfandroid.pomodaily.presentation.resource.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleFragment: Fragment() {

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ScheduleViewModel>()

    private val todoTaskAdapter by lazy {
        TaskScheduleAdapter(
            onItemClick = { taskId->
                viewModel.setActiveTask(taskId)
            }
        )
    }

    private val doneTaskAdapter by lazy {
        TaskScheduleAdapter(
            onItemClick = { _ -> }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
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
            appBar.tvTitle.text = getString(R.string.txt_schedule)
            appBar.btnBack.setOnClickListener { findNavController().navigateUp() }

            // RV
            rvToDo.layoutManager = LinearLayoutManager(requireContext())
            rvToDo.adapter = todoTaskAdapter
            rvToDo.addItemDecoration(
                RecyclerViewLinearItemDecoration(
                    topSpace = resources.getDimensionPixelSize(R.dimen.dimen_0),
                    bottomSpace = resources.getDimensionPixelSize(R.dimen.dimen_16),
                    rightSpace = resources.getDimensionPixelSize(R.dimen.dimen_0),
                    leftSpace = resources.getDimensionPixelSize(R.dimen.dimen_0),
                    addBottomSpacingForLastItem = true
                )
            )

            rvDone.layoutManager = LinearLayoutManager(requireContext())
            rvDone.adapter = doneTaskAdapter
            rvDone.addItemDecoration(
                RecyclerViewLinearItemDecoration(
                    topSpace = resources.getDimensionPixelSize(R.dimen.dimen_0),
                    bottomSpace = resources.getDimensionPixelSize(R.dimen.dimen_16),
                    rightSpace = resources.getDimensionPixelSize(R.dimen.dimen_0),
                    leftSpace = resources.getDimensionPixelSize(R.dimen.dimen_0),
                    addBottomSpacingForLastItem = true
                )
            )
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isTasksEmpty.collectLatest {
                        binding.svContent.isVisible = !it
                        binding.layoutError.apply {
                            root.isVisible = it
                            ivIllustration.setImageResource(R.drawable.img_illustration_empty)
                            tvTitle.text = getString(R.string.txt_empty_data)
                            tvDesc.text = getString(R.string.txt_msg_no_tasks_for_today)
                            btnAction.visibility = View.GONE
                        }
                    }
                }

                launch {
                    viewModel.todoTasks.collectLatest {
                        if (it is UiState.Success) {
                            binding.tvToDo.isVisible = !it.data.isNullOrEmpty()
                            binding.rvToDo.isVisible = !it.data.isNullOrEmpty()
                            todoTaskAdapter.submitList(it.data)
                        }
                    }
                }

                launch {
                    viewModel.doneTasks.collectLatest {
                        if (it is UiState.Success) {
                            binding.tvDone.isVisible = !it.data.isNullOrEmpty()
                            binding.rvDone.isVisible = !it.data.isNullOrEmpty()
                            doneTaskAdapter.submitList(it.data)
                        }
                    }
                }
            }
        }
    }
}