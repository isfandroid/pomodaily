package com.isfandroid.pomodaily.utils

import com.isfandroid.pomodaily.TaskCompletionLogEntity
import com.isfandroid.pomodaily.TaskEntity
import com.isfandroid.pomodaily.data.model.Task
import com.isfandroid.pomodaily.data.model.TaskCompletionLog
import com.isfandroid.pomodaily.presentation.model.ExpandableTaskUiModel

object DataMapper {

    fun mapLocalTaskToDomain(input: TaskEntity) = Task(
        id = input.id.toInt(),
        dayOfWeek = input.dayOfWeek.toInt(),
        order = input.order.toInt(),
        name = input.name,
        pomodoroSessions = input.pomodoroSessions.toInt(),
        completedSessions = input.completedSessions.toInt(),
        note = input.note
    )

    fun mapDomainTaskToLocal(input: Task) = TaskEntity(
        id = (input.id ?: 0).toLong(),
        dayOfWeek = input.dayOfWeek.toLong(),
        order = (input.order ?: 0).toLong(),
        name = input.name.orEmpty(),
        pomodoroSessions = input.pomodoroSessions.toLong(),
        completedSessions = input.completedSessions.toLong(),
        note = input.note
    )

    fun mapDomainTaskToExpandableTaskUiModel(input: Task) = ExpandableTaskUiModel(
        id = input.id,
        dayOfWeek = input.dayOfWeek,
        order = input.order,
        name = input.name,
        pomodoroSessions = input.pomodoroSessions,
        completedSessions = input.completedSessions,
        note = input.note,
        isExpanded = false,
        isNewEntry = false
    )

    fun mapExpandableTaskUiModelToDomain(input: ExpandableTaskUiModel) = Task(
        id = input.id,
        dayOfWeek = input.dayOfWeek,
        order = input.order,
        name = input.name,
        pomodoroSessions = input.pomodoroSessions ?: 0,
        completedSessions = input.completedSessions ?: 0,
        note = input.note
    )

    fun mapDomainTaskCompletionLogToLocal(input: TaskCompletionLog) = TaskCompletionLogEntity(
        id = input.id,
        taskId = input.taskId,
        completionDate = input.completionDate
    )
}