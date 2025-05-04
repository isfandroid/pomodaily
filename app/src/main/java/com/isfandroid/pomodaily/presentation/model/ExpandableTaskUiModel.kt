package com.isfandroid.pomodaily.presentation.model

import com.isfandroid.pomodaily.data.model.Task

data class ExpandableTaskUiModel(
    val task: Task,
    var isExpanded: Boolean = false
)