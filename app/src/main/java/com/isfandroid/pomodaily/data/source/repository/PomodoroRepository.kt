package com.isfandroid.pomodaily.data.source.repository

import com.isfandroid.pomodaily.data.model.TimerData
import com.isfandroid.pomodaily.data.source.local.LocalDataSource
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_IDLE
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_RUNNING
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_BREAK
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_POMODORO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PomodoroRepository @Inject constructor(
    private val localDataSource: LocalDataSource
) {
    private val _timerData = MutableStateFlow(
        TimerData(
            remainingTime = 0,
            type = TIMER_TYPE_POMODORO,
            state = TIMER_STATE_IDLE
        )
    )
    val timerData = _timerData.asStateFlow()

    fun setTimerState(state: String) {
        _timerData.update { it.copy(state = state) }
    }

    fun setTimerType(type: String) {
        _timerData.update { it.copy(type = type) }
    }

    fun updateRemainingTime(remainingTime: Int) {
        if (_timerData.value.state == TIMER_STATE_RUNNING) {
            _timerData.update { it.copy(remainingTime = remainingTime) }
        }
    }

    suspend fun resetTimerForCurrentType() {
        val durationMinutes = when (_timerData.value.type) {
            TIMER_TYPE_POMODORO -> localDataSource.pomodoroDuration.first()
            TIMER_TYPE_BREAK -> localDataSource.breakDuration.first()
            else -> localDataSource.longBreakDuration.first()
        }
        _timerData.update { it.copy(remainingTime = durationMinutes * 60) }
    }
}