package com.isfandroid.pomodaily.presentation.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.source.repository.PomodoroRepository
import com.isfandroid.pomodaily.data.source.repository.SettingsRepository
import com.isfandroid.pomodaily.utils.Constant.APP_THEME_LIGHT
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
    private val settingsRepository: SettingsRepository,
    private val pomodoroRepository: PomodoroRepository,
): ViewModel() {

    // Timer
    val pomodoroDuration = settingsRepository.pomodoroDuration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_POMODORO_MINUTES)
    val breakDuration = settingsRepository.breakDuration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_BREAK_MINUTES)
    val longBreakDuration = settingsRepository.longBreakDuration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_LONG_BREAK_MINUTES)
    val longBreakInterval = settingsRepository.longBreakInterval
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_LONG_BREAK_INTERVAL)
    val autoStartPomodoros = settingsRepository.autoStartPomodoros
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_AUTO_START_POMODOROS)
    val autoStartBreaks = settingsRepository.autoStartBreaks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_AUTO_START_BREAKS)

    fun setPomodoroDuration(value: Int) {
        viewModelScope.launch {
            settingsRepository.setPomodoroDuration(value)
            pomodoroRepository.resetTimerForCurrentType()
        }
    }
    fun setBreakDuration(value: Int) {
        viewModelScope.launch {
            settingsRepository.setBreakDuration(value)
            pomodoroRepository.resetTimerForCurrentType()
        }
    }
    fun setLongBreakDuration(value: Int) {
        viewModelScope.launch {
            settingsRepository.setLongBreakDuration(value)
            pomodoroRepository.resetTimerForCurrentType()
        }
    }
    fun setLongBreakInterval(value: Int) {
        viewModelScope.launch { settingsRepository.setLongBreakInterval(value) }
    }
    fun setAutoStartBreaks(value: Boolean) {
        viewModelScope.launch { settingsRepository.setAutoStartBreaks(value) }
    }
    fun setAutoStartPomodoros(value: Boolean) {
        viewModelScope.launch { settingsRepository.setAutoStartPomodoros(value) }
    }

    // General
    val appTheme = settingsRepository.appTheme
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), APP_THEME_LIGHT)

    fun setAppTheme(value: String) {
        viewModelScope.launch { settingsRepository.setAppTheme(value) }
    }
}