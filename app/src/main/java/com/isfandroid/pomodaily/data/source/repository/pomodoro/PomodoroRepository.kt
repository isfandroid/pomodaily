package com.isfandroid.pomodaily.data.source.repository.pomodoro

import com.isfandroid.pomodaily.data.model.TimerData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface PomodoroRepository {

    fun getTimerData(): StateFlow<TimerData>

    fun setTimerState(state: String)

    fun setTimerType(type: String)

    fun updateRemainingTime(remainingTime: Int)

    suspend fun resetTimerForCurrentType()

    fun getPomodoroCount(): Flow<Int>
    suspend fun setPomodoroCount(value: Int)

    fun getPomodoroDuration(): Flow<Int>
    suspend fun setPomodoroDuration(value: Int)

    fun getBreakDuration(): Flow<Int>
    suspend fun setBreakDuration(value: Int)

    fun getLongBreakDuration(): Flow<Int>
    suspend fun setLongBreakDuration(value: Int)

    fun getLongBreakInterval(): Flow<Int>
    suspend fun setLongBreakInterval(value: Int)

    fun getAutoStartPomodoros(): Flow<Boolean>
    suspend fun setAutoStartPomodoros(value: Boolean)

    fun getAutoStartBreaks(): Flow<Boolean>
    suspend fun setAutoStartBreaks(value: Boolean)
}