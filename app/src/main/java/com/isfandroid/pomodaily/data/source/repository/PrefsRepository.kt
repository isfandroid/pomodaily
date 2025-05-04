package com.isfandroid.pomodaily.data.source.repository

import com.isfandroid.pomodaily.data.source.local.LocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefsRepository @Inject constructor(
    private val localDataSource: LocalDataSource
) {

    val isOnBoardingDone = localDataSource.isOnBoardingDone
    suspend fun setIsOnBoardingDone(value: Boolean) {
        localDataSource.setIsOnBoardingDone(value)
    }

    val pomodoroDuration = localDataSource.pomodoroDuration
    suspend fun setPomodoroDuration(value: Int) {
        localDataSource.setPomodoroDuration(value)
    }

    val breakDuration = localDataSource.breakDuration
    suspend fun setBreakDuration(value: Int) {
        localDataSource.setBreakDuration(value)
    }

    val setLongBreakDuration = localDataSource.longBreakDuration
    suspend fun setLongBreakDuration(value: Int) {
        localDataSource.setLongBreakDuration(value)
    }
}