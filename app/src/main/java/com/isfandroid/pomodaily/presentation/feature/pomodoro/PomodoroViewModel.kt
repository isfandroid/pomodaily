package com.isfandroid.pomodaily.presentation.feature.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.model.TimerData
import com.isfandroid.pomodaily.data.source.repository.pomodoro.PomodoroRepository
import com.isfandroid.pomodaily.data.source.repository.task.TaskRepository
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_POMODORO_MINUTES
import com.isfandroid.pomodaily.utils.Constant.STATE_IN_TIMEOUT_MS
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_IDLE
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_POMODORO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val pomodoroRepository: PomodoroRepository,
    taskRepository: TaskRepository,
): ViewModel() {

    val activeTask = taskRepository.getActiveTask()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), null)

    val timerData: StateFlow<TimerData> = pomodoroRepository.getTimerData()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS),
            TimerData (
                remainingTime = 0,
                type = TIMER_TYPE_POMODORO,
                state = TIMER_STATE_IDLE
            )
        )

    val pomodoroDuration: StateFlow<Int> = pomodoroRepository.getPomodoroDuration()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS),
            DEFAULT_POMODORO_MINUTES
        )

    init {
        viewModelScope.launch {
            pomodoroRepository.resetTimerForCurrentType()
        }
    }
}