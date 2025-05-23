package com.isfandroid.pomodaily.data.source.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.isfandroid.pomodaily.data.source.local.model.TaskCompletionLogEntity
import com.isfandroid.pomodaily.data.source.local.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {

    @Query("SELECT * FROM tasks WHERE dayOfWeek = :dayId ORDER BY `order` ASC")
    fun getTasksByDay(dayId: Int): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    fun getTask(id: Long): Flow<TaskEntity?>

    @Query("SELECT * FROM tasks WHERE dayOfWeek = :dayId AND completedSessions < pomodoroSessions ORDER BY `order` ASC LIMIT 1")
    fun getUncompletedTaskByDay(dayId: Int): Flow<TaskEntity?>

    @Query("SELECT DISTINCT dayOfWeek FROM tasks")
    fun getDaysWithTasks(): Flow<List<Int>>

    @Query("SELECT COUNT(*) FROM task_completion_logs WHERE completionDate >= :startTimeMillis AND completionDate <= :endTimeMillis")
    fun getTotalCompletedTasksBetween(startTimeMillis: Long, endTimeMillis: Long): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE dayOfWeek = :dayId")
    fun getTotalTasksByDay(dayId: Int): Flow<Int>

    @Query("UPDATE tasks SET completedSessions = 0 WHERE dayOfWeek = :dayId")
    suspend fun resetTasksCompletedSessionsForDay(dayId: Int)

    @Upsert
    suspend fun upsertTask(task: TaskEntity)

    @Upsert
    suspend fun upsertTasks(tasks: List<TaskEntity>)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Insert
    suspend fun insertTaskCompletionLog(completionLog: TaskCompletionLogEntity)

    @Query("DELETE FROM task_completion_logs WHERE taskId = :taskId")
    suspend fun deleteTaskCompletionLogsByTaskId(taskId: Long)
}