package com.isfandroid.pomodaily.data.source.repository.pomodoro

import com.isfandroid.pomodaily.data.model.TimerData
import com.isfandroid.pomodaily.data.source.local.pomodoro.PomodoroLocalDataSource
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_IDLE
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_RUNNING
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_BREAK
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_POMODORO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

class PomodoroRepositoryImpl (
    private val localDataSource: PomodoroLocalDataSource
): PomodoroRepository {

    private val _timerData = MutableStateFlow(
        TimerData(
            remainingTime = 0,
            type = TIMER_TYPE_POMODORO,
            state = TIMER_STATE_IDLE
        )
    )
    override fun getTimerData() = _timerData.asStateFlow()

    override fun setTimerState(state: String) {
        _timerData.update { it.copy(state = state) }
    }

    override fun setTimerType(type: String) {
        _timerData.update { it.copy(type = type) }
    }

    override fun updateRemainingTime(remainingTime: Int) {
        if (_timerData.value.state == TIMER_STATE_RUNNING) {
            _timerData.update { it.copy(remainingTime = remainingTime) }
        }
    }

    override suspend fun resetTimerForCurrentType() {
        val durationMinutes = when (_timerData.value.type) {
            TIMER_TYPE_POMODORO -> localDataSource.getPomodoroDuration().first()
            TIMER_TYPE_BREAK -> localDataSource.getBreakDuration().first()
            else -> localDataSource.getLongBreakDuration().first()
        }
        _timerData.update { it.copy(remainingTime = durationMinutes * 60) }
    }

    override fun getPomodoroCount() = localDataSource.getPomodoroCount()
        .catch { emit(0) }

    override suspend fun setPomodoroCount(value: Int) = localDataSource.setPomodoroCount(value)

    override fun getPomodoroDuration() = localDataSource.getPomodoroDuration()
    .catch { emit(0) }

    override suspend fun setPomodoroDuration(value: Int) = localDataSource.setPomodoroDuration(value)

    override fun getBreakDuration()= localDataSource.getBreakDuration()
    .catch { emit(0) }

    override suspend fun setBreakDuration(value: Int) = localDataSource.setBreakDuration(value)

    override fun getLongBreakDuration()= localDataSource.getLongBreakDuration()
    .catch { emit(0) }

    override suspend fun setLongBreakDuration(value: Int) = localDataSource.setLongBreakDuration(value)

    override fun getLongBreakInterval()= localDataSource.getLongBreakInterval()
    .catch { emit(0) }

    override suspend fun setLongBreakInterval(value: Int) = localDataSource.setLongBreakInterval(value)

    override fun getAutoStartPomodoros() = localDataSource.getAutoStartPomodoros()
    .catch { emit(false) }

    override suspend fun setAutoStartPomodoros(value: Boolean) = localDataSource.setAutoStartPomodoros(value)

    override fun getAutoStartBreaks() = localDataSource.getAutoStartBreaks()
    .catch { emit(false) }

    override suspend fun setAutoStartBreaks(value: Boolean) = localDataSource.setAutoStartBreaks(value)
}