package com.isfandroid.pomodaily.data.source.repository

import com.isfandroid.pomodaily.data.model.Task
import com.isfandroid.pomodaily.data.resource.Result
import com.isfandroid.pomodaily.data.source.local.LocalDataSource
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

    fun getTasksByDay(dayId:Int): Flow<Result<List<Task>>> = localDataSource.getTasksByDay(dayId)
        .map {
            val tasks = it.map { mapLocalTaskToDomain(it) }
            Result.Success(tasks) as Result<List<Task>>
        }
        .catch { e ->
            emit(Result.Error(e.message ?: "Unknown error occurred: Get Tasks by Day"))
        }

    fun getTask(taskId: Long): Flow<Result<Task>> = flow {
        try {
            val task = localDataSource.getTask(taskId).first().let { mapLocalTaskToDomain(it) }
            emit(Result.Success(task))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred: Get Task"))
        }
    }

    fun getUncompletedTaskByDay(dayId: Int): Flow<Result<Task?>> = flow {
        try {
            val result = localDataSource.getUncompletedTaskByDay(dayId).first()
            if (result == null) {
                emit(Result.Success(null))
            } else {
                emit(Result.Success(mapLocalTaskToDomain(result)))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred: Get Uncompleted Task by Day"))
        }
    }

    fun upsertTask(task: Task): Flow<Result<Unit>> = flow {
        try {
            val localTask = mapDomainTaskToLocal(task)
            localDataSource.upsertTask(localTask)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred: Add or Update Task"))
        }
    }.flowOn(Dispatchers.IO)

    fun upsertTasks(tasks: List<Task>): Flow<Result<Unit>> = flow {
        try {
            val localTasks = tasks.map { mapDomainTaskToLocal(it) }
            localDataSource.upsertTasks(localTasks)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred: Add or Update Tasks"))
        }
    }.flowOn(Dispatchers.IO)

    fun deleteTask(task: Task): Flow<Result<Unit>> = flow {
        try {
            val localTask = mapDomainTaskToLocal(task)
            localDataSource.deleteTask(localTask)
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred: Delete Task"))
        }
    }.flowOn(Dispatchers.IO)
}