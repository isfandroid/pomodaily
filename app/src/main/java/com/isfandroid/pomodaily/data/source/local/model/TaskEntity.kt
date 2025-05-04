package com.isfandroid.pomodaily.data.source.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val dayOfWeek: Int,
    val order: Int,
    val name: String,
    val pomodoroSessions: Int,
    val note: String? = null,
)
