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

    val pomodoroCount = localDataSource.pomodoroCount
    suspend fun setPomodoroCount(value: Int) {
        localDataSource.setPomodoroCount(value)
    }

    val pomodoroDuration = localDataSource.pomodoroDuration
    suspend fun setPomodoroDuration(value: Int) {
        localDataSource.setPomodoroDuration(value)
    }

    val breakDuration = localDataSource.breakDuration
    suspend fun setBreakDuration(value: Int) {
        localDataSource.setBreakDuration(value)
    }

    val longBreakDuration = localDataSource.longBreakDuration
    suspend fun setLongBreakDuration(value: Int) {
        localDataSource.setLongBreakDuration(value)
    }

    val longBreakInterval = localDataSource.longBreakInterval
    suspend fun setLongBreakInterval(value: Int) {
        localDataSource.setLongBreakInterval(value)
    }

    val autoStartBreaks = localDataSource.autoStartBreaks
    suspend fun setAutoStartBreaks(value: Boolean) {
        localDataSource.setAutoStartBreaks(value)
    }

    val autoStartPomodoros = localDataSource.autoStartPomodoros
    suspend fun setAutoStartPomodoros(value: Boolean) {
        localDataSource.setAutoStartPomodoros(value)
    }

    val activeTaskId = localDataSource.activeTaskId
    suspend fun setActiveTaskId(value: Long) {
        localDataSource.setActiveTaskId(value)
    }

    val lastResetDate = localDataSource.lastResetDate
    suspend fun setLastResetDate(value: Long) {
        localDataSource.setLastResetDate(value)
    }
}