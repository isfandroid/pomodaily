package com.isfandroid.pomodaily.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.isfandroid.pomodaily.data.source.local.database.AppDao
import com.isfandroid.pomodaily.data.source.local.model.TaskEntity
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_BREAK_DURATION
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_IS_ON_BOARDING_DONE
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_LONG_BREAK_DURATION
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_POMODORO_DURATION
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val appDao: AppDao
) {

    /** region APP PREFS **/
    suspend fun setIsOnBoardingDone(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[booleanPreferencesKey(PREFS_KEY_IS_ON_BOARDING_DONE)] = value
        }
    }
    val isOnBoardingDone: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[booleanPreferencesKey(PREFS_KEY_IS_ON_BOARDING_DONE)] ?: false
    }

    suspend fun setPomodoroDuration(minutes: Int) {
        dataStore.edit { prefs ->
            prefs[intPreferencesKey(PREFS_KEY_POMODORO_DURATION)] = minutes
        }
    }
    val pomodoroDuration: Flow<Int> = dataStore.data.map { prefs ->
        prefs[intPreferencesKey(PREFS_KEY_POMODORO_DURATION)] ?: 25
    }

    suspend fun setBreakDuration(minutes: Int) {
        dataStore.edit { prefs ->
            prefs[intPreferencesKey(PREFS_KEY_BREAK_DURATION)] = minutes
        }
    }
    val breakDuration: Flow<Int> = dataStore.data.map { prefs ->
        prefs[intPreferencesKey(PREFS_KEY_BREAK_DURATION)] ?: 5
    }

    suspend fun setLongBreakDuration(minutes: Int) {
        dataStore.edit { prefs ->
            prefs[intPreferencesKey(PREFS_KEY_LONG_BREAK_DURATION)] = minutes
        }
    }
    val longBreakDuration: Flow<Int> = dataStore.data.map { prefs ->
        prefs[intPreferencesKey(PREFS_KEY_LONG_BREAK_DURATION)] ?: 10
    }
    /** endregion APP PREFS **/

    /** region DB - TASK **/
    fun getTasksByDay(dayId: Int) = appDao.getTasksByDay(dayId)
    suspend fun upsertTasks(tasks: List<TaskEntity>) = appDao.upsertTasks(tasks)
    suspend fun upsertTask(task: TaskEntity) = appDao.upsertTask(task)
    suspend fun deleteTask(task: TaskEntity) = appDao.deleteTask(task)
    /** endregion DB - TASK **/
}