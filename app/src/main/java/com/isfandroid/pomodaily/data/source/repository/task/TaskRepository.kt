package com.isfandroid.pomodaily.data.source.repository.task

import com.isfandroid.pomodaily.data.model.Task
import com.isfandroid.pomodaily.data.model.TaskCompletionLog
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTasksByDay(dayId: Int): Flow<List<Task>>

    fun getActiveTask(): Flow<Task?>

    fun getUncompletedTaskByDay(dayId: Int): Flow<Task?>

    fun getDaysWithTasks(): Flow<List<Int>>

    fun getTotalTasksByDay(): Flow<Map<Int, Int>>

    fun getTodayTotalCompletedTasks(): Flow<Int>
    fun getYesterdayTotalCompletedTasks(): Flow<Int>

    fun getThisWeekTotalCompletedTasks(): Flow<Int>
    fun getLastWeekTotalCompletedTasks(): Flow<Int>

    fun getThisMonthTotalCompletedTasks(): Flow<Int>
    fun getLastMonthTotalCompletedTasks(): Flow<Int>

    suspend fun resetTasksCompletedSessionsForDay(dayId: Int)

    suspend fun insertTask(task: Task)

    suspend fun copyTasks(fromDay: Int, toDay: Int)

    suspend fun updateTask(task: Task)

    suspend fun updateActiveTaskId(id: Int)

    suspend fun deleteTask(id: Int)

    suspend fun insertTaskCompletionLog(log: TaskCompletionLog)

    suspend fun deleteTaskCompletionLogsByTaskId(taskId: Int)

    suspend fun deleteTaskCompletionLogsOlderThan(cutoffTimestampMillis: Long)
}