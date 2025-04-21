package com.isfandroid.pomodaily.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.isfandroid.pomodaily.utils.Constant.PREFS_KEY_IS_ON_BOARDING_DONE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    /** region APP PREFS - FIRST TIME **/
    suspend fun setIsOnBoardingDone(value: Boolean) {
        dataStore.edit { prefs ->
            prefs[booleanPreferencesKey(PREFS_KEY_IS_ON_BOARDING_DONE)] = value
        }
    }
    val isOnBoardingDone: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[booleanPreferencesKey(PREFS_KEY_IS_ON_BOARDING_DONE)] ?: true
    }
    /** endregion APP PREFS - FIRST TIME **/
}