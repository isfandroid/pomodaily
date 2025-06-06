package com.isfandroid.pomodaily.data.source.local.general

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

class GeneralLocalDataSourceImpl (
    private val dataStore: DataStore<Preferences>
): GeneralLocalDataSource {

    override fun getIsOnBoardingDone() = dataStore.data.map { prefs ->
        prefs[booleanPreferencesKey(PREFS_KEY_IS_ON_BOARDING_DONE)] ?: false
    }

    override suspend fun setIsOnBoardingDone(value: Boolean) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[booleanPreferencesKey(PREFS_KEY_IS_ON_BOARDING_DONE)] = value
            }
        }
    }

    override fun getLastResetDate() = dataStore.data.map { prefs ->
        prefs[longPreferencesKey(PREFS_KEY_LAST_RESET_DATE)] ?: 0L
    }

    override suspend fun setLastResetDate(value: Long) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[longPreferencesKey(PREFS_KEY_LAST_RESET_DATE)] = value
            }
        }
    }
}