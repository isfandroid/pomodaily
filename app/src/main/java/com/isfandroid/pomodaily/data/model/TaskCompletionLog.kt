package com.isfandroid.pomodaily.data.model

data class TaskCompletionLog(
    val id: Long = 0L,
    val taskId: Long,
    val completionDate: Long,
)
