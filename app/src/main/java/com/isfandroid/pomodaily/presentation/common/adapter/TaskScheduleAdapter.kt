package com.isfandroid.pomodaily.presentation.common.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.ItemTaskScheduleBinding
import com.isfandroid.pomodaily.presentation.model.TaskScheduleUiModel

class TaskScheduleAdapter(
    private val onItemClick: (Int?) -> Unit,
): ListAdapter<TaskScheduleUiModel, TaskScheduleAdapter.TaskViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskScheduleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    inner class TaskViewHolder(private val binding: ItemTaskScheduleBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: TaskScheduleUiModel) {
            val isDone = data.completedSessions == data.pomodoroSessions

            with(binding) {
                // Style
                cardParent.strokeWidth = if (data.isActive) itemView.context.resources.getDimensionPixelSize(R.dimen.dimen_2) else 0
                tvName.alpha = if (isDone) 0.5f else 1f
                tvSessions.alpha = if (isDone) 0.5f else 1f
                tvRemainingTime.alpha = if (isDone) 0.5f else 1f
                tvName.paintFlags = if (isDone) {
                    binding.tvName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    binding.tvName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                // Content
                tvName.text = data.name
                tvSessions.text = itemView.context.getString(
                    R.string.txt_value_task_sessions,
                    data.completedSessions,
                    data.pomodoroSessions
                )
                tvRemainingTime.text = if (isDone) {
                    itemView.context.getString(R.string.txt_done)
                } else {
                    itemView.context.getString(R.string.txt_value_minutes_left, data.remainingTimeMinutes)
                }

                // Click Listeners
                if (!isDone) {
                    itemView.setOnClickListener {
                        onItemClick.invoke(if (data.isActive) null else data.id)
                    }
                }
            }
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<TaskScheduleUiModel>() {
            override fun areItemsTheSame(oldItem: TaskScheduleUiModel, newItem: TaskScheduleUiModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TaskScheduleUiModel, newItem: TaskScheduleUiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}