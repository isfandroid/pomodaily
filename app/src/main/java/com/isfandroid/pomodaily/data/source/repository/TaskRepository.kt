package com.isfandroid.pomodaily.data.source.repository

import com.isfandroid.pomodaily.data.model.Task
import com.isfandroid.pomodaily.data.resource.Result
import com.isfandroid.pomodaily.data.source.local.LocalDataSource
import com.isfandroid.pomodaily.utils.DataMapper.mapDomainTaskToLocal
import com.isfandroid.pomodaily.utils.DataMapper.mapLocalTaskToDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(
    private val localDataSource: LocalDataSource
) {

    fun getTasksByDay(dayId: Int): Flow<Result<List<Task>>> = flow {
        try {
            val tasks = localDataSource.getTasksByDay(dayId).first().map { mapLocalTaskToDomain(it) }
            emit(Result.Success(tasks))
        } catch (e: Exception) {
            emit(Result.Error(e.message ?: "Unknown error occurred: Get Tasks"))
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