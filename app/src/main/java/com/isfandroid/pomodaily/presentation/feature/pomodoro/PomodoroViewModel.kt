package com.isfandroid.pomodaily.presentation.feature.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.model.Task
import com.isfandroid.pomodaily.data.model.TimerData
import com.isfandroid.pomodaily.data.resource.Result
import com.isfandroid.pomodaily.data.source.repository.PomodoroRepository
import com.isfandroid.pomodaily.data.source.repository.TaskRepository
import com.isfandroid.pomodaily.presentation.resource.UiState
import com.isfandroid.pomodaily.utils.Constant.STATE_IN_TIMEOUT_MS
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_IDLE
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_POMODORO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val pomodoroRepository: PomodoroRepository,
    taskRepository: TaskRepository,
): ViewModel() {

    val activeTask: StateFlow<UiState<Task?>> = taskRepository.getActiveTask()
        .map {
            when(it) {
                is Result.Error -> UiState.Error(it.message)
                is Result.Success -> UiState.Success(it.data)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), UiState.Loading())

    val timerData: StateFlow<TimerData> = pomodoroRepository.timerData
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS),
            TimerData (
                remainingTime = 0,
                type = TIMER_TYPE_POMODORO,
                state = TIMER_STATE_IDLE
            )
        )

    init {
        viewModelScope.launch {
            pomodoroRepository.resetTimerForCurrentType()
        }
    }
}