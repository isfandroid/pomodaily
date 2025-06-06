package com.isfandroid.pomodaily.data.source.local.task

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.isfandroid.pomodaily.AppDatabase
import com.isfandroid.pomodaily.TaskCompletionLogEntity
import com.isfandroid.pomodaily.TaskEntity
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_ACTIVE_TASK_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TaskLocalDataSourceImpl (
    private val dataStore: DataStore<Preferences>,
    database: AppDatabase
): TaskLocalDataSource {

    private val tasksQueries = database.taskEntityQueries
    private val logsQueries = database.taskCompletionLogEntityQueries

    override fun getTasksByDay(dayId: Int): Flow<List<TaskEntity>> =
        tasksQueries.getTasksByDay(dayId.toLong())
            .asFlow()
            .mapToList(Dispatchers.IO)

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getActiveTask(): Flow<TaskEntity?> {
        val activeTaskId = dataStore.data.map { prefs ->
            prefs[longPreferencesKey(PREFS_KEY_ACTIVE_TASK_ID)] ?: 0L
        }

        return activeTaskId.flatMapLatest { id ->
            if (id == 0L) {
                flowOf(null)
            } else {
                tasksQueries.getTaskById(id)
                    .asFlow()
                    .mapToOneOrNull(Dispatchers.IO)
            }
        }
    }

    override fun getUncompletedTaskByDay(dayId: Int): Flow<TaskEntity?>  =
        tasksQueries.getUncompletedTaskByDay(dayId.toLong())
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)

    override fun getDaysWithTasks(): Flow<List<Int>> =
        tasksQueries.getDaysWithTasks()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toInt() } }

    override fun getTotalTasksByDay(dayId: Int): Flow<Int> =
        tasksQueries.getTotalTasksByDay(dayId.toLong())
            .asFlow()
            .mapToOne(Dispatchers.IO)
            .map { it.toInt() }

    override fun getTotalCompletedTasksBetween(
        startTimeMillis: Long,
        endTimeMillis: Long
    ) = logsQueries.getTotalLogsBetween(startTimeMillis, endTimeMillis)
        .asFlow()
        .mapToOne(Dispatchers.IO)
        .map { it.toInt() }

    override suspend fun resetTasksCompletedSessionsForDay(dayId: Int) {
        withContext(Dispatchers.IO) {
            tasksQueries.resetTasksCompletedSessionsForDay(dayId.toLong())
        }
    }

    override suspend fun updateTask(task: TaskEntity) {
        withContext(Dispatchers.IO) {
            tasksQueries.updateTask(
                id = task.id,
                order = task.order,
                name = task.name,
                pomodoroSessions = task.pomodoroSessions,
                completedSessions = task.completedSessions,
                note = task.note,
            )
        }
    }

    override suspend fun insertTask(task: TaskEntity) {
        withContext(Dispatchers.IO) {
            val maxOrderForDay = tasksQueries.getMaxOrderForDay(task.dayOfWeek).executeAsOneOrNull()?.maxOrder ?: 0

            tasksQueries.insertTask(
                dayId = task.dayOfWeek,
                order = maxOrderForDay+1,
                name = task.name,
                pomodoroSessions = task.pomodoroSessions,
                completedSessions = task.completedSessions,
                note = task.note,
            )
        }
    }

    override suspend fun insertTasks(tasks: List<TaskEntity>) {
        withContext(Dispatchers.IO) {
            tasksQueries.transaction {
                tasks.forEachIndexed { index, task ->
                    tasksQueries.insertTask(
                        dayId = task.dayOfWeek,
                        order = (index+1).toLong(),
                        name = task.name,
                        pomodoroSessions = task.pomodoroSessions,
                        completedSessions = task.completedSessions,
                        note = task.note,
                    )
                }
            }
        }
    }

    override suspend fun updateActiveTaskId(id: Int) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[longPreferencesKey(PREFS_KEY_ACTIVE_TASK_ID)] = id.toLong()
            }
        }
    }

    override suspend fun deleteTask(id: Int) {
        withContext(Dispatchers.IO) {
            tasksQueries.deleteTaskById(id.toLong())
        }
    }

    override suspend fun insertTaskCompletionLog(log: TaskCompletionLogEntity) {
        withContext(Dispatchers.IO) {
            logsQueries.insertLog(
                taskId = log.taskId,
                completionDate = log.completionDate
            )
        }
    }

    override suspend fun deleteTaskCompletionLogsByTaskId(taskId: Int) {
        withContext(Dispatchers.IO) {
            logsQueries.deleteLogsByTaskId(taskId.toLong())
        }
    }

    override suspend fun deleteTaskCompletionLogsOlderThan(cutoffTimestampMillis: Long) {
        withContext(Dispatchers.IO) {
            logsQueries.deleteLogsOlderThan(cutoffTimestampMillis)
        }
    }
}