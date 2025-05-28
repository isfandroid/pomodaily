package com.isfandroid.pomodaily.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import com.isfandroid.pomodaily.data.source.local.database.AppDao
import com.isfandroid.pomodaily.data.source.local.model.TaskCompletionLogEntity
import com.isfandroid.pomodaily.data.source.local.model.TaskEntity
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_AUTO_START_BREAKS
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_AUTO_START_POMODOROS
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_BREAK_MINUTES
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_LONG_BREAK_INTERVAL
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_LONG_BREAK_MINUTES
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_POMODORO_COUNT
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_POMODORO_MINUTES
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_ACTIVE_TASK_ID
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_BREAK_DURATION
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_IS_ON_BOARDING_DONE
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_LAST_RESET_DATE
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_LONG_BREAK_DURATION
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_LONG_BREAK_INTERVAL
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_POMODORO_COUNT
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_POMODORO_DURATION
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_SETTINGS_AUTO_START_BREAKS
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_SETTINGS_AUTO_START_POMODOROS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class LocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val appDao: AppDao
) {

    /** region APP PREFS - OnBoarding **/
    suspend fun setIsOnBoardingDone(value: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(PREFS_KEY_IS_ON_BOARDING_DONE)] = value
            }
        }
    }
    val isOnBoardingDone: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[booleanPreferencesKey(PREFS_KEY_IS_ON_BOARDING_DONE)] ?: false
    }
    /** endregion APP PREFS - OnBoarding **/

    /** region APP PREFS - Pomodoro **/
    suspend fun setPomodoroCount(value: Int) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[intPreferencesKey(PREFS_KEY_POMODORO_COUNT)] = value
            }
        }
    }
    val pomodoroCount: Flow<Int> = dataStore.data.map { prefs ->
        prefs[intPreferencesKey(PREFS_KEY_POMODORO_COUNT)] ?: DEFAULT_POMODORO_COUNT
    }

    suspend fun setPomodoroDuration(minutes: Int) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[intPreferencesKey(PREFS_KEY_POMODORO_DURATION)] = minutes
            }
        }
    }
    val pomodoroDuration: Flow<Int> = dataStore.data.map { prefs ->
        prefs[intPreferencesKey(PREFS_KEY_POMODORO_DURATION)] ?: DEFAULT_POMODORO_MINUTES
    }

    suspend fun setBreakDuration(minutes: Int) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[intPreferencesKey(PREFS_KEY_BREAK_DURATION)] = minutes
            }
        }
    }
    val breakDuration: Flow<Int> = dataStore.data.map { prefs ->
        prefs[intPreferencesKey(PREFS_KEY_BREAK_DURATION)] ?: DEFAULT_BREAK_MINUTES
    }

    suspend fun setLongBreakDuration(minutes: Int) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[intPreferencesKey(PREFS_KEY_LONG_BREAK_DURATION)] = minutes
            }
        }
    }
    val longBreakDuration: Flow<Int> = dataStore.data.map { prefs ->
        prefs[intPreferencesKey(PREFS_KEY_LONG_BREAK_DURATION)] ?: DEFAULT_LONG_BREAK_MINUTES
    }

    suspend fun setLongBreakInterval(value: Int) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[intPreferencesKey(PREFS_KEY_LONG_BREAK_INTERVAL)] = value
            }
        }
    }
    val longBreakInterval: Flow<Int> = dataStore.data.map { prefs ->
        prefs[intPreferencesKey(PREFS_KEY_LONG_BREAK_INTERVAL)] ?: DEFAULT_LONG_BREAK_INTERVAL
    }
    /** endregion APP PREFS - Pomodoro **/

    /** region APP PREFS - Settings **/
    suspend fun setAutoStartBreaks(value: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(PREFS_KEY_SETTINGS_AUTO_START_BREAKS)] = value
            }
        }
    }
    val autoStartBreaks: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[booleanPreferencesKey(PREFS_KEY_SETTINGS_AUTO_START_BREAKS)] ?: DEFAULT_AUTO_START_BREAKS
    }

    suspend fun setAutoStartPomodoros(value: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(PREFS_KEY_SETTINGS_AUTO_START_POMODOROS)] = value
            }
        }
    }
    val autoStartPomodoros: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[booleanPreferencesKey(PREFS_KEY_SETTINGS_AUTO_START_POMODOROS)] ?: DEFAULT_AUTO_START_POMODOROS
    }

    suspend fun setLastResetDate(value: Long) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[longPreferencesKey(PREFS_KEY_LAST_RESET_DATE)] = value
            }
        }
    }
    val lastResetDate: Flow<Long> = dataStore.data.map { prefs ->
        prefs[longPreferencesKey(PREFS_KEY_LAST_RESET_DATE)] ?: 0L
    }
    /** endregion APP PREFS - Settings **/

    /** region APP PREFS - Task **/
    suspend fun setActiveTaskId(value: Long) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[longPreferencesKey(PREFS_KEY_ACTIVE_TASK_ID)] = value
            }
        }
    }
    val activeTaskId: Flow<Long> = dataStore.data.map { prefs ->
        prefs[longPreferencesKey(PREFS_KEY_ACTIVE_TASK_ID)] ?: 0L
    }
    /** endregion APP PREFS - Task **/

    /** region DB - TASK **/
    fun getTasksByDay(dayId: Int) = appDao.getTasksByDay(dayId)
    fun getActiveTask() = activeTaskId.flatMapLatest { appDao.getTask(it) }
    fun getUncompletedTaskByDay(dayId: Int) = appDao.getUncompletedTaskByDay(dayId)
    fun getDaysWithTasks() = appDao.getDaysWithTasks()
    fun getTotalTasksByDay(dayId: Int) = appDao.getTotalTasksByDay(dayId)
    fun getTotalCompletedTasksBetween(startTimeMillis: Long, endTimeMillis: Long) = appDao.getTotalLogsBetween(startTimeMillis, endTimeMillis)
    suspend fun resetTaskCompletedSessionsForDay(dayId: Int) = appDao.resetTasksCompletedSessionsForDay(dayId)
    suspend fun upsertTask(task: TaskEntity) = appDao.upsertTask(task)
    suspend fun upsertTasks(tasks: List<TaskEntity>) = appDao.upsertTasks(tasks)
    suspend fun deleteTask(task: TaskEntity) = appDao.deleteTask(task)
    suspend fun insertTaskCompletionLog(log: TaskCompletionLogEntity) = appDao.insertLog(log)
    suspend fun deleteTaskCompletionLogsByTaskId(taskId: Long) = appDao.deleteLogsByTaskId(taskId)
    suspend fun deleteTaskCompletionLogsOlderThan(cutoffTimestampMillis: Long) = appDao.deleteLogsOlderThan(cutoffTimestampMillis)
    /** endregion DB - TASK **/
}