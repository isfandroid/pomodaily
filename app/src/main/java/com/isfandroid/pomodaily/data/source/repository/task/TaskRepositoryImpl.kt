package com.isfandroid.pomodaily.data.source.repository.task

import com.isfandroid.pomodaily.data.model.Task
import com.isfandroid.pomodaily.data.model.TaskCompletionLog
import com.isfandroid.pomodaily.data.source.local.task.TaskLocalDataSource
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

class TaskRepositoryImpl (
    private val taskLocalDataSource: TaskLocalDataSource
): TaskRepository {

    override fun getTasksByDay(dayId: Int) = taskLocalDataSource.getTasksByDay(dayId)
        .map { localTasks ->
            localTasks.map { mapLocalTaskToDomain(it) }
        }
        .catch { emit(emptyList()) }

    override fun getActiveTask() = taskLocalDataSource.getActiveTask()
        .map { if (it == null) null else mapLocalTaskToDomain(it) }
        .catch { emit(null) }

    override fun getUncompletedTaskByDay(dayId: Int) = taskLocalDataSource.getUncompletedTaskByDay(dayId)
        .map { if (it == null) null else mapLocalTaskToDomain(it) }
        .catch { emit(null) }

    override fun getDaysWithTasks() = taskLocalDataSource.getDaysWithTasks()
        .catch { emit(emptyList()) }

    private val _daysOfWeekFlows = DAYS_OF_WEEK.map { day ->
        val dayId = day["id"] as Int
        taskLocalDataSource.getTotalTasksByDay(dayId).map { total ->
            dayId to total
        }
    }
    override fun getTotalTasksByDay() = combine(_daysOfWeekFlows) { totalPerDayArray ->
        totalPerDayArray.toMap()
    }

    override fun getTodayTotalCompletedTasks() =
        taskLocalDataSource.getTotalCompletedTasksBetween(
            DateUtils.getTodayStartMillis(),
            DateUtils.getTodayEndMillis()
        ).catch { emit(0) }

    override fun getYesterdayTotalCompletedTasks() =
        taskLocalDataSource.getTotalCompletedTasksBetween(
            DateUtils.getYesterdayStartMillis(),
            DateUtils.getYesterdayEndMillis()
        ).catch { emit(0) }

    override fun getThisWeekTotalCompletedTasks() =
        taskLocalDataSource.getTotalCompletedTasksBetween(
            DateUtils.getThisWeekStartMillis(),
            DateUtils.getThisWeekEndMillis()
        ).catch { emit(0) }

    override fun getLastWeekTotalCompletedTasks() =
        taskLocalDataSource.getTotalCompletedTasksBetween(
            DateUtils.getLastWeekStartMillis(),
            DateUtils.getLastWeekEndMillis()
        ).catch { emit(0) }

    override fun getThisMonthTotalCompletedTasks() =
        taskLocalDataSource.getTotalCompletedTasksBetween(
            DateUtils.getThisMonthStartMillis(),
            DateUtils.getThisMonthEndMillis()
        ).catch { emit(0) }

    override fun getLastMonthTotalCompletedTasks() =
        taskLocalDataSource.getTotalCompletedTasksBetween(
            DateUtils.getLastMonthStartMillis(),
            DateUtils.getLastMonthEndMillis()
        ).catch { emit(0) }

    override suspend fun resetTasksCompletedSessionsForDay(dayId: Int) =
        taskLocalDataSource.resetTasksCompletedSessionsForDay(dayId)

    override suspend fun insertTask(task: Task) =
        taskLocalDataSource.insertTask(mapDomainTaskToLocal(task))


    override suspend fun copyTasks(fromDay: Int, toDay: Int) {
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

    override suspend fun updateTask(task: Task) =
        taskLocalDataSource.updateTask(mapDomainTaskToLocal(task))


    override suspend fun updateActiveTaskId(id: Int) =
        taskLocalDataSource.updateActiveTaskId(id)

    override suspend fun deleteTask(id: Int) =
        taskLocalDataSource.deleteTask(id)

    override suspend fun insertTaskCompletionLog(log: TaskCompletionLog) =
        taskLocalDataSource.insertTaskCompletionLog(mapDomainTaskCompletionLogToLocal(log))

    override suspend fun deleteTaskCompletionLogsByTaskId(taskId: Int) =
        taskLocalDataSource.deleteTaskCompletionLogsByTaskId(taskId)

    override suspend fun deleteTaskCompletionLogsOlderThan(cutoffTimestampMillis: Long) =
        taskLocalDataSource.deleteTaskCompletionLogsOlderThan(cutoffTimestampMillis)
}