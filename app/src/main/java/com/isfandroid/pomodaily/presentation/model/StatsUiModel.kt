package com.isfandroid.pomodaily.presentation.model

data class StatsUiModel(
    val completedTasks: Int,
    val totalTasks: Int,
    val completionRate: Int,
    val previousCompletedTasks: Int,
    val previousTotalTasks: Int,
    val previousCompletionRate: Int,
)