package com.isfandroid.pomodaily.presentation.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.source.repository.PomodoroRepository
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_AUTO_START_BREAKS
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_AUTO_START_POMODOROS
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_BREAK_MINUTES
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_LONG_BREAK_INTERVAL
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_LONG_BREAK_MINUTES
import com.isfandroid.pomodaily.utils.Constant.STATE_IN_TIMEOUT_MS
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_POMODORO_MINUTES
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val pomodoroRepository: PomodoroRepository,
): ViewModel() {

    // Timer
    val pomodoroDuration = pomodoroRepository.getPomodoroDuration()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_POMODORO_MINUTES)
    val breakDuration = pomodoroRepository.getBreakDuration()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_BREAK_MINUTES)
    val longBreakDuration = pomodoroRepository.getLongBreakDuration()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_LONG_BREAK_MINUTES)
    val longBreakInterval = pomodoroRepository.getLongBreakInterval()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_LONG_BREAK_INTERVAL)
    val autoStartPomodoros = pomodoroRepository.getAutoStartPomodoros()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_AUTO_START_POMODOROS)
    val autoStartBreaks = pomodoroRepository.getAutoStartBreaks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_AUTO_START_BREAKS)

    fun setPomodoroDuration(value: Int) {
        viewModelScope.launch {
            pomodoroRepository.setPomodoroDuration(value)
            pomodoroRepository.resetTimerForCurrentType()
        }
    }
    fun setBreakDuration(value: Int) {
        viewModelScope.launch {
            pomodoroRepository.setBreakDuration(value)
            pomodoroRepository.resetTimerForCurrentType()
        }
    }
    fun setLongBreakDuration(value: Int) {
        viewModelScope.launch {
            pomodoroRepository.setLongBreakDuration(value)
            pomodoroRepository.resetTimerForCurrentType()
        }
    }
    fun setLongBreakInterval(value: Int) {
        viewModelScope.launch { pomodoroRepository.setLongBreakInterval(value) }
    }
    fun setAutoStartBreaks(value: Boolean) {
        viewModelScope.launch { pomodoroRepository.setAutoStartBreaks(value) }
    }
    fun setAutoStartPomodoros(value: Boolean) {
        viewModelScope.launch { pomodoroRepository.setAutoStartPomodoros(value) }
    }
}