package com.isfandroid.pomodaily.data.model

data class Task(
    val id: Int? = null,
    val dayOfWeek: Int?,
    val order: Int?,
    val name: String?,
    val pomodoroSessions: Int = 1,
    val completedSessions: Int = 0,
    val note: String? = null
)
