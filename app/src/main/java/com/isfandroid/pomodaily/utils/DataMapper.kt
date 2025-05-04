package com.isfandroid.pomodaily.utils

import com.isfandroid.pomodaily.data.model.Task
import com.isfandroid.pomodaily.data.source.local.model.TaskEntity

object DataMapper {

    fun mapLocalTaskToDomain(input: TaskEntity) = Task(
        id = input.id.toInt(),
        dayOfWeek = input.dayOfWeek,
        order = input.order,
        name = input.name,
        pomodoroSessions = input.pomodoroSessions,
        note = input.note
    )

    fun mapDomainTaskToLocal(input: Task) = TaskEntity(
        id = (input.id ?: 0).toLong(),
        dayOfWeek = input.dayOfWeek ?: 0,
        order = input.order ?: 0,
        name = input.name.orEmpty(),
        pomodoroSessions = input.pomodoroSessions,
        note = input.note
    )
}