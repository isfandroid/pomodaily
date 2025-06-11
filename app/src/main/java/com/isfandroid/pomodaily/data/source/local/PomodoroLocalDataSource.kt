package com.isfandroid.pomodaily.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_AUTO_START_BREAKS
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_AUTO_START_POMODOROS
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_BREAK_MINUTES
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_LONG_BREAK_INTERVAL
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_LONG_BREAK_MINUTES
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_POMODORO_COUNT
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_POMODORO_MINUTES
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_BREAK_DURATION
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_LONG_BREAK_DURATION
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_LONG_BREAK_INTERVAL
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_POMODORO_COUNT
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_POMODORO_DURATION
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_SETTINGS_AUTO_START_BREAKS
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_SETTINGS_AUTO_START_POMODOROS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PomodoroLocalDataSource (
    private val dataStore: DataStore<Preferences>
) {

    fun getPomodoroCount() = dataStore.data.map { prefs ->
        prefs[intPreferencesKey(PREFS_KEY_POMODORO_COUNT)] ?: DEFAULT_POMODORO_COUNT
    }

    suspend fun setPomodoroCount(value: Int) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[intPreferencesKey(PREFS_KEY_POMODORO_COUNT)] = value
            }
        }
    }

    fun getPomodoroDuration() = dataStore.data.map { prefs ->
        prefs[intPreferencesKey(PREFS_KEY_POMODORO_DURATION)] ?: DEFAULT_POMODORO_MINUTES
    }

    suspend fun setPomodoroDuration(value: Int) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[intPreferencesKey(PREFS_KEY_POMODORO_DURATION)] = value
            }
        }
    }

    fun getBreakDuration() = dataStore.data.map { prefs ->
        prefs[intPreferencesKey(PREFS_KEY_BREAK_DURATION)] ?: DEFAULT_BREAK_MINUTES
    }

    suspend fun setBreakDuration(value: Int) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[intPreferencesKey(PREFS_KEY_BREAK_DURATION)] = value
            }
        }
    }

    fun getLongBreakDuration() = dataStore.data.map { prefs ->
        prefs[intPreferencesKey(PREFS_KEY_LONG_BREAK_DURATION)] ?: DEFAULT_LONG_BREAK_MINUTES
    }

    suspend fun setLongBreakDuration(value: Int) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[intPreferencesKey(PREFS_KEY_LONG_BREAK_DURATION)] = value
            }
        }
    }

    fun getLongBreakInterval() = dataStore.data.map { prefs ->
        prefs[intPreferencesKey(PREFS_KEY_LONG_BREAK_INTERVAL)] ?: DEFAULT_LONG_BREAK_INTERVAL
    }

    suspend fun setLongBreakInterval(value: Int) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[intPreferencesKey(PREFS_KEY_LONG_BREAK_INTERVAL)] = value
            }
        }
    }

    fun getAutoStartPomodoros() = dataStore.data.map { prefs ->
        prefs[booleanPreferencesKey(PREFS_KEY_SETTINGS_AUTO_START_POMODOROS)] ?: DEFAULT_AUTO_START_POMODOROS
    }

    suspend fun setAutoStartPomodoros(value: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(PREFS_KEY_SETTINGS_AUTO_START_POMODOROS)] = value
            }
        }
    }

    fun getAutoStartBreaks() = dataStore.data.map { prefs ->
        prefs[booleanPreferencesKey(PREFS_KEY_SETTINGS_AUTO_START_BREAKS)] ?: DEFAULT_AUTO_START_BREAKS
    }

    suspend fun setAutoStartBreaks(value: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(PREFS_KEY_SETTINGS_AUTO_START_BREAKS)] = value
            }
        }
    }
}