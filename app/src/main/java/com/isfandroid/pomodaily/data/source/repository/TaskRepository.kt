package com.isfandroid.pomodaily.data.source.repository

import com.isfandroid.pomodaily.data.model.Task
import com.isfandroid.pomodaily.data.resource.Result
import com.isfandroid.pomodaily.data.source.local.LocalDataSource
import com.isfandroid.pomodaily.utils.Constant.CURRENT_DAY
import com.isfandroid.pomodaily.utils.DataMapper.mapDomainTaskToLocal
import com.isfandroid.pomodaily.utils.DataMapper.mapLocalTaskToDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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

    fun getTasksByDay(dayId: Int): Flow<Result<List<Task>>> = localDataSource.getTasksByDay(dayId)
        .map { localTasks ->
            val tasks = localTasks.map { mapLocalTaskToDomain(it) }
            Result.Success(tasks) as Result<List<Task>>
        }
        .catch { e ->
            emit(Result.Error(e.message ?: "Unknown error occurred: Get Tasks by Day Id $dayId"))
        }

    fun getActiveTask(): Flow<Result<Task?>> = localDataSource.getActiveTask()
        .map {
            if (it == null) {
                Result.Success(null)
            } else {
                val task = mapLocalTaskToDomain(it)
                Result.Success(task) as Result<Task>
            }
        }.catch { e ->
            emit(Result.Error(e.message ?: "Unknown error occurred: Get Active Task"))
        }

    fun getUncompletedTaskByDay(dayId: Int) = localDataSource.getUncompletedTaskByDay(dayId)
        .map {
            if (it == null) {
                Result.Success(null)
            } else {
                val task = mapLocalTaskToDomain(it)
                Result.Success(task) as Result<Task>
            }
        }.catch { e ->
            emit(Result.Error(e.message ?: "Unknown error occurred: Get Uncompleted Task by Day Id $dayId"))
        }

    fun getDaysWithTasks() = localDataSource.getDaysWithTasks()
        .map {
            Result.Success(it) as Result<List<Int>>
        }.catch { e ->
            emit(Result.Error(e.message ?: "Unknown error occurred: Get Days with Tasks"))
        }

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
            // Set new entry task as active task if no tasks set to active
            if (task.id == 0 && localDataSource.activeTaskId.first() == 0L) {
                val uncompletedTask = localDataSource.getUncompletedTaskByDay(CURRENT_DAY).first()
                localDataSource.setActiveTaskId(uncompletedTask?.id ?: 0L)
            }

            // Map domain to local task
            val localTask = mapDomainTaskToLocal(task)
            localDataSource.upsertTask(localTask)

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
}