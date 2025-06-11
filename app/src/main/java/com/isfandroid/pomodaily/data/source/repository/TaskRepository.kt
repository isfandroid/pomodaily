package com.isfandroid.pomodaily.data.source.repository

import com.isfandroid.pomodaily.data.model.Task
import com.isfandroid.pomodaily.data.model.TaskCompletionLog
import com.isfandroid.pomodaily.data.source.local.TaskLocalDataSource
import com.isfandroid.pomodaily.utils.DataMapper.mapDomainTaskCompletionLogToLocal
import com.isfandroid.pomodaily.utils.DataMapper.mapDomainTaskToLocal
import com.isfandroid.pomodaily.utils.DataMapper.mapLocalTaskToDomain
import com.isfandroid.pomodaily.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import com.isfandroid.pomodaily.utils.DateUtils.DAYS_OF_WEEK

class TaskRepository (
    private val taskLocalDataSource: TaskLocalDataSource
) {

    fun getTasksByDay(dayId: Int) = taskLocalDataSource.getTasksByDay(dayId)
        .map { localTasks ->
            localTasks.map { mapLocalTaskToDomain(it) }
        }
        .catch { emit(emptyList()) }

    fun getActiveTask() = taskLocalDataSource.getActiveTask()
        .map { if (it == null) null else mapLocalTaskToDomain(it) }
        .catch { emit(null) }

    fun getUncompletedTaskByDay(dayId: Int) = taskLocalDataSource.getUncompletedTaskByDay(dayId)
        .map { if (it == null) null else mapLocalTaskToDomain(it) }
        .catch { emit(null) }

    fun getDaysWithTasks() = taskLocalDataSource.getDaysWithTasks()
        .catch { emit(emptyList()) }

    private val _daysOfWeekFlows = DAYS_OF_WEEK.map { day ->
        val dayId = day["id"] as Int
        taskLocalDataSource.getTotalTasksByDay(dayId).map { total ->
            dayId to total
        }
    }
    fun getTotalTasksByDay() = combine(_daysOfWeekFlows) { totalPerDayArray ->
        totalPerDayArray.toMap()
    }

    fun getTodayTotalCompletedTasks() =
        taskLocalDataSource.getTotalCompletedTasksBetween(
            DateUtils.getTodayStartMillis(),
            DateUtils.getTodayEndMillis()
        ).catch { emit(0) }

    fun getYesterdayTotalCompletedTasks() =
        taskLocalDataSource.getTotalCompletedTasksBetween(
            DateUtils.getYesterdayStartMillis(),
            DateUtils.getYesterdayEndMillis()
        ).catch { emit(0) }

    fun getThisWeekTotalCompletedTasks() =
        taskLocalDataSource.getTotalCompletedTasksBetween(
            DateUtils.getThisWeekStartMillis(),
            DateUtils.getThisWeekEndMillis()
        ).catch { emit(0) }

    fun getLastWeekTotalCompletedTasks() =
        taskLocalDataSource.getTotalCompletedTasksBetween(
            DateUtils.getLastWeekStartMillis(),
            DateUtils.getLastWeekEndMillis()
        ).catch { emit(0) }

    fun getThisMonthTotalCompletedTasks() =
        taskLocalDataSource.getTotalCompletedTasksBetween(
            DateUtils.getThisMonthStartMillis(),
            DateUtils.getThisMonthEndMillis()
        ).catch { emit(0) }

    fun getLastMonthTotalCompletedTasks() =
        taskLocalDataSource.getTotalCompletedTasksBetween(
            DateUtils.getLastMonthStartMillis(),
            DateUtils.getLastMonthEndMillis()
        ).catch { emit(0) }

    suspend fun resetTasksCompletedSessionsForDay(dayId: Int) =
        taskLocalDataSource.resetTasksCompletedSessionsForDay(dayId)

    suspend fun insertTask(task: Task) =
        taskLocalDataSource.insertTask(mapDomainTaskToLocal(task))


    suspend fun copyTasks(fromDay: Int, toDay: Int) {
        withContext(Dispatchers.IO) {
            val tasksSource = taskLocalDataSource.getTasksByDay(fromDay).first()
            val newTasks = tasksSource.map {
                it.copy(
                    id = 0L,
                    completedSessions = 0,
                    dayOfWeek = toDay.toLong()
                )
            }
            taskLocalDataSource.insertTasks(newTasks)
        }
    }

    suspend fun updateTask(task: Task) =
        taskLocalDataSource.updateTask(mapDomainTaskToLocal(task))


    suspend fun updateActiveTaskId(id: Int) =
        taskLocalDataSource.updateActiveTaskId(id)

    suspend fun deleteTask(id: Int) =
        taskLocalDataSource.deleteTask(id)

    suspend fun insertTaskCompletionLog(log: TaskCompletionLog) =
        taskLocalDataSource.insertTaskCompletionLog(mapDomainTaskCompletionLogToLocal(log))

    suspend fun deleteTaskCompletionLogsByTaskId(taskId: Int) =
        taskLocalDataSource.deleteTaskCompletionLogsByTaskId(taskId)

    suspend fun deleteTaskCompletionLogsOlderThan(cutoffTimestampMillis: Long) =
        taskLocalDataSource.deleteTaskCompletionLogsOlderThan(cutoffTimestampMillis)
}