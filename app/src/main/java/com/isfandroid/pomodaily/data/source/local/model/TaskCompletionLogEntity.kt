package com.isfandroid.pomodaily.data.source.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_completion_logs")
data class TaskCompletionLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val taskId: Long,
    val completionDate: Long,
)

