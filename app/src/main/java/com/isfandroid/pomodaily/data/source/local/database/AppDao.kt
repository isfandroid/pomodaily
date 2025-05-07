package com.isfandroid.pomodaily.data.source.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.isfandroid.pomodaily.data.source.local.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Query("SELECT * FROM tasks WHERE dayOfWeek = :dayId ORDER BY `order` ASC")
    fun getTasksByDay(dayId: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTask(id: Long): Flow<TaskEntity>

    @Query("SELECT * FROM tasks WHERE dayOfWeek = :dayId AND completedSessions < pomodoroSessions ORDER BY `order` ASC LIMIT 1")
    fun getUncompletedTaskByDay(dayId: Int): Flow<TaskEntity?>

    @Upsert
    suspend fun upsertTask(task: TaskEntity)

    @Upsert
    suspend fun upsertTasks(tasks: List<TaskEntity>)

    @Delete
    suspend fun deleteTask(task: TaskEntity)
}