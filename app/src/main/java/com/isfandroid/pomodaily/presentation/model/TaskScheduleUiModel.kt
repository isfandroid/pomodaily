package com.isfandroid.pomodaily.presentation.model

data class TaskScheduleUiModel(
    val id: Int,
    val name: String,
    val completedSessions: Int,
    val pomodoroSessions: Int,
    val remainingTimeMinutes: Int,
    val isActive: Boolean,
)
