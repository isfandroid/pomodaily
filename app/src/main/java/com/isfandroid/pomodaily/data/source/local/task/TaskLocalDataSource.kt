package com.isfandroid.pomodaily.data.source.local.task

import com.isfandroid.pomodaily.TaskCompletionLogEntity
import com.isfandroid.pomodaily.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskLocalDataSource {

    fun getTasksByDay(dayId: Int): Flow<List<TaskEntity>>

    fun getActiveTask(): Flow<TaskEntity?>

    fun getUncompletedTaskByDay(dayId: Int): Flow<TaskEntity?>

    fun getDaysWithTasks(): Flow<List<Int>>

    fun getTotalTasksByDay(dayId: Int): Flow<Int>

    fun getTotalCompletedTasksBetween(startTimeMillis: Long, endTimeMillis: Long): Flow<Int>

    suspend fun resetTasksCompletedSessionsForDay(dayId: Int)

    suspend fun insertTask(task: TaskEntity)

    suspend fun insertTasks(tasks: List<TaskEntity>)

    suspend fun updateTask(task: TaskEntity)

    suspend fun updateActiveTaskId(id: Int)

    suspend fun deleteTask(id: Int)

    suspend fun insertTaskCompletionLog(log: TaskCompletionLogEntity)

    suspend fun deleteTaskCompletionLogsByTaskId(taskId: Int)

    suspend fun deleteTaskCompletionLogsOlderThan(cutoffTimestampMillis: Long)
}