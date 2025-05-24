package com.isfandroid.pomodaily.data.source.repository

import com.isfandroid.pomodaily.data.model.Task
import com.isfandroid.pomodaily.data.model.TaskCompletionLog
import com.isfandroid.pomodaily.data.resource.Result
import com.isfandroid.pomodaily.data.source.local.LocalDataSource
import com.isfandroid.pomodaily.utils.DataMapper.mapDomainTaskCompletionLogToLocal
import com.isfandroid.pomodaily.utils.DataMapper.mapDomainTaskToLocal
import com.isfandroid.pomodaily.utils.DataMapper.mapLocalTaskToDomain
import com.isfandroid.pomodaily.utils.DateUtils
import com.isfandroid.pomodaily.utils.DateUtils.CURRENT_DAY
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val localDataSource: LocalDataSource
) {
    /** region ATTRIBUTES **/
    val totalTasksCompletedToday = localDataSource.getTotalCompletedTasksBetween(
        DateUtils.getTodayStartMillis(), DateUtils.getTodayEndMillis()
    )
    val totalTasksCompletedYesterday = localDataSource.getTotalCompletedTasksBetween(
        DateUtils.getYesterdayStartMillis(), DateUtils.getYesterdayEndMillis()
    )

    val totalTasksCompletedThisWeek = localDataSource.getTotalCompletedTasksBetween(
        DateUtils.getThisWeekStartMillis(), DateUtils.getThisWeekEndMillis()
    )
    val totalTasksCompletedLastWeek = localDataSource.getTotalCompletedTasksBetween(
        DateUtils.getLastWeekStartMillis(), DateUtils.getLastWeekEndMillis()
    )

    val totalTasksCompletedThisMonth = localDataSource.getTotalCompletedTasksBetween(
        DateUtils.getThisMonthStartMillis(), DateUtils.getThisMonthEndMillis()
    )
    val totalTasksCompletedLastMonth = localDataSource.getTotalCompletedTasksBetween(
        DateUtils.getLastMonthStartMillis(), DateUtils.getLastMonthEndMillis()
    )

    private val dayOfWeekFlows = (0..6).map { day ->
        localDataSource.getTotalTasksByDay(day).map { total ->
            day to total
        }
    }
    val totalTasksByDayMapped: Flow<Map<Int, Int>> = combine(dayOfWeekFlows) { totalPerDayArray ->
        totalPerDayArray.toMap()
    }

    val activeTask = localDataSource.getActiveTask()
        .map { if (it == null) null else mapLocalTaskToDomain(it) }
        .catch { emit(null) }

    val daysWithTasks = localDataSource.getDaysWithTasks()

    fun getTasksByDay(dayId: Int) = localDataSource.getTasksByDay(dayId)
        .map { localTasks ->
            localTasks.map { mapLocalTaskToDomain(it) }
        }
        .catch { emit(emptyList()) }

    fun getUncompletedTaskByDay(dayId: Int) = localDataSource.getUncompletedTaskByDay(dayId)
        .map { if (it == null) null else mapLocalTaskToDomain(it) }
        .catch { emit(null) }
    /** endregion ATTRIBUTES **/

    /** region ACTIONS **/
    fun setActiveTask(taskId: Long?): Flow<Result<Unit>> = flow {
        try {
            localDataSource.setActiveTaskId(taskId ?: 0L)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred: Set Active Task to $taskId"))
        }
    }.flowOn(Dispatchers.IO)

    fun upsertTask(task: Task): Flow<Result<Unit>> = flow {
        try {
            // Map domain to local task
            val localTask = mapDomainTaskToLocal(task)
            localDataSource.upsertTask(localTask)

            // Set new entry task as active task if no tasks set to active
            if (task.id == 0 && localDataSource.activeTaskId.first() == 0L) {
                val uncompletedTask = localDataSource.getUncompletedTaskByDay(CURRENT_DAY).first()
                localDataSource.setActiveTaskId(uncompletedTask?.id ?: 0L)
            }

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred: Add or Update Task $task"))
        }
    }.flowOn(Dispatchers.IO)

    fun copyTasks(fromDay: Int, toDay: Int): Flow<Result<Unit>> = flow {
        try {
            val tasksSource = localDataSource.getTasksByDay(fromDay).first()
            val newTasks = tasksSource.map {
                it.copy(
                    id = 0L,
                    completedSessions = 0,
                    dayOfWeek = toDay
                )
            }
            localDataSource.upsertTasks(newTasks)

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred: Copy Task from $fromDay to $toDay"))
        }
    }.flowOn(Dispatchers.IO)

    fun deleteTask(task: Task): Flow<Result<Unit>> = flow {
        try {
            val localTask = mapDomainTaskToLocal(task)
            localDataSource.deleteTask(localTask)
            localDataSource.deleteTaskCompletionLogsByTaskId((task.id ?: 0).toLong())

            // Set new active task if the deleted task is the current active task
            if (localDataSource.activeTaskId.first() == task.id?.toLong()) {
                val uncompletedTask = localDataSource.getUncompletedTaskByDay(CURRENT_DAY).first()
                localDataSource.setActiveTaskId(uncompletedTask?.id ?: 0L)
            }

            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred: Delete Task $task"))
        }
    }.flowOn(Dispatchers.IO)

    fun resetTasksCompletedSessionsForDay(dayId: Int): Flow<Result<Unit>> = flow {
        try {
            localDataSource.resetTaskCompletedSessionsForDay(dayId)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred: Reset Tasks Completed Sessions for Day Id $dayId"))
        }
    }.flowOn(Dispatchers.IO)

    fun insertTaskCompletionLog(log: TaskCompletionLog): Flow<Result<Unit>> = flow {
        try {
            val localLog = mapDomainTaskCompletionLogToLocal(log)
            localDataSource.insertTaskCompletionLog(localLog)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred: Insert Task Completion Log"))
        }
    }.flowOn(Dispatchers.IO)
    /** endregion ACTIONS **/
}