package com.isfandroid.pomodaily.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_IS_ON_BOARDING_DONE
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_LAST_RESET_DATE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class GeneralLocalDataSource (
    private val dataStore: DataStore<Preferences>
) {

    fun getIsOnBoardingDone() = dataStore.data.map { prefs ->
        prefs[booleanPreferencesKey(PREFS_KEY_IS_ON_BOARDING_DONE)] ?: false
    }

    suspend fun setIsOnBoardingDone(value: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(PREFS_KEY_IS_ON_BOARDING_DONE)] = value
            }
        }
    }

    fun getLastResetDate() = dataStore.data.map { prefs ->
        prefs[longPreferencesKey(PREFS_KEY_LAST_RESET_DATE)] ?: 0L
    }

    suspend fun setLastResetDate(value: Long) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[longPreferencesKey(PREFS_KEY_LAST_RESET_DATE)] = value
            }
        }
    }
}