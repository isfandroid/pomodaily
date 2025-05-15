package com.isfandroid.pomodaily.presentation.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.databinding.ItemTaskEditableBinding
import com.isfandroid.pomodaily.presentation.model.ExpandableTaskUiModel

class ExpandableTaskAdapter(
    private val onItemClick: (ExpandableTaskUiModel) -> Unit,
    private val onDeleteClick: (ExpandableTaskUiModel) -> Unit,
    private val onCancelClick: (ExpandableTaskUiModel) -> Unit,
    private val onSaveClick: (ExpandableTaskUiModel) -> Unit,
): ListAdapter<ExpandableTaskUiModel, ExpandableTaskAdapter.TaskViewHolder>(COMPARATOR) {

    private var itemPomodoroSessions: Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskEditableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val data = getItem(position)
        itemPomodoroSessions = data.pomodoroSessions ?: 0
        holder.bind(data)

    }

    inner class TaskViewHolder(private val binding: ItemTaskEditableBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ExpandableTaskUiModel) {
            with(binding) {
                tvFormTitleTaskName.isVisible = data.isExpanded
                tilTaskName.isVisible = data.isExpanded
                tvFormTitleTaskPomodoroSessions.isVisible = data.isExpanded
                tilTaskPomodoroSessions.isVisible = data.isExpanded
                btnIncrement.isVisible = data.isExpanded
                btnDecrement.isVisible = data.isExpanded
                tvFormTitleTaskNotes.isVisible = data.isExpanded
                tilTaskNotes.isVisible = data.isExpanded
                btnDelete.isVisible = data.isExpanded && !data.isNewEntry
                btnCancel.isVisible = data.isExpanded
                btnSave.isVisible = data.isExpanded

                tvTaskName.isVisible = !data.isExpanded
                tvTaskPomodoroSessions.isVisible = !data.isExpanded
                tvTaskNotes.isVisible = !data.isExpanded && !data.note.isNullOrEmpty()

                if (data.isExpanded) {
                    etTaskName.setText(data.name)
                    etTaskPomodoroSessions.setText(data.pomodoroSessions.toString())
                    etTaskPomodoroSessions.doOnTextChanged { _, _, _, _ ->
                        if (etTaskPomodoroSessions.text.toString().isNotEmpty()) {
                            itemPomodoroSessions = etTaskPomodoroSessions.text.toString().toInt()
                            if (itemPomodoroSessions > 10) etTaskPomodoroSessions.setText(10.toString())
                        } else {
                            itemPomodoroSessions = 0
                        }
                    }
                    etTaskNotes.setText(data.note.orEmpty())
                } else {
                    tvTaskName.text = data.name
                    tvTaskPomodoroSessions.text = itemView.context.getString(R.string.txt_value_pomodoro_sessions, data.pomodoroSessions)
                    tvTaskNotes.text = data.note.orEmpty()
                }

                tvTaskNotes.setOnClickListener {
                    if (tvTaskNotes.lineCount > 1) {
                        if (tvTaskNotes.maxLines == 2) {
                            tvTaskNotes.maxLines = Int.MAX_VALUE
                        } else {
                            tvTaskNotes.maxLines = 2
                        }
                    }
                }
                btnIncrement.setOnClickListener {
                    if (!tilTaskPomodoroSessions.error.isNullOrEmpty()) tilTaskPomodoroSessions.error = ""
                    if (itemPomodoroSessions < 10) itemPomodoroSessions++
                    etTaskPomodoroSessions.setText(itemPomodoroSessions.toString())
                }
                btnDecrement.setOnClickListener {
                    if (!tilTaskPomodoroSessions.error.isNullOrEmpty()) tilTaskPomodoroSessions.error = ""
                    if (itemPomodoroSessions > 1) itemPomodoroSessions--
                    etTaskPomodoroSessions.setText(itemPomodoroSessions.toString())
                }

                itemView.setOnClickListener {
                    clearFormsFocus()
                    onItemClick.invoke(data)
                }
                btnDelete.setOnClickListener {
                    clearFormsFocus()
                    onDeleteClick.invoke(data)
                }
                btnCancel.setOnClickListener {
                    clearFormsFocus()
                    onCancelClick.invoke(data)
                }
                btnSave.setOnClickListener {
                    clearFormsFocus()

                    val nameEntry = etTaskName.text.toString().trim()
                    val pomodoroSessionsEntry = etTaskPomodoroSessions.text.toString()
                    val notesEntry = etTaskNotes.text.toString().trim()

                    when {
                        nameEntry.isEmpty() -> tilTaskName.error = itemView.context.getString(R.string.txt_msg_field_required)
                        pomodoroSessionsEntry.isEmpty() -> tilTaskPomodoroSessions.error = itemView.context.getString(R.string.txt_msg_field_required)
                        else -> {
                            val updatedTask = data.copy(
                                name = nameEntry,
                                pomodoroSessions = pomodoroSessionsEntry.toInt(),
                                note = notesEntry.ifEmpty { null }
                            )
                            onSaveClick.invoke(updatedTask)
                        }
                    }
                }
            }
        }

        private fun clearFormsFocus() {
            with(binding) {
                tilTaskName.clearFocus()
                etTaskName.clearFocus()
                tilTaskPomodoroSessions.clearFocus()
                etTaskPomodoroSessions.clearFocus()
                tilTaskNotes.clearFocus()
                etTaskNotes.clearFocus()
            }

            val imm: InputMethodManager = itemView.context.getSystemService(InputMethodManager::class.java)
            imm.hideSoftInputFromWindow(itemView.windowToken, 0)
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<ExpandableTaskUiModel>() {
            override fun areItemsTheSame(oldItem: ExpandableTaskUiModel, newItem: ExpandableTaskUiModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ExpandableTaskUiModel, newItem: ExpandableTaskUiModel): Boolean {
                return oldItem == newItem
            }
        }
    }
}