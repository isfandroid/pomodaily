package com.isfandroid.pomodaily.presentation.feature.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.model.Task
import com.isfandroid.pomodaily.data.resource.Result
import com.isfandroid.pomodaily.data.source.repository.PrefsRepository
import com.isfandroid.pomodaily.data.source.repository.TaskRepository
import com.isfandroid.pomodaily.presentation.resource.UiState
import com.isfandroid.pomodaily.utils.Constant.CURRENT_DAY
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_AUTO_START_BREAKS
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_AUTO_START_POMODOROS
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_BREAK_MINUTES
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_LONG_BREAK_INTERVAL
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_LONG_BREAK_MINUTES
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_POMODORO_COUNT
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_POMODORO_MINUTES
import com.isfandroid.pomodaily.utils.Constant.STATE_IN_TIMEOUT_MS
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_IDLE
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_PAUSED
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_RUNNING
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_BREAK
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_LONG_BREAK
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_POMODORO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val prefsRepository: PrefsRepository,
    private val taskRepository: TaskRepository,
): ViewModel() {

    val activeTask: StateFlow<UiState<Task?>> = taskRepository.getActiveTask()
        .map {
            when(it) {
                is Result.Error -> UiState.Error(it.message)
                is Result.Success -> UiState.Success(it.data)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), UiState.Loading())

    private val nextUncompletedTask: StateFlow<UiState<Task?>> = taskRepository.getUncompletedTaskByDay(CURRENT_DAY)
        .map {
            when(it) {
                is Result.Success -> UiState.Success(it.data)
                is Result.Error -> UiState.Error(it.message)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), UiState.Loading())

    private val _remainingTimeSeconds = MutableStateFlow(0)
    val remainingTimeSeconds = _remainingTimeSeconds.asStateFlow()

    private val _timerState = MutableStateFlow(TIMER_STATE_IDLE)
    val timerState = _timerState.asStateFlow()

    private val _timerType = MutableStateFlow(TIMER_TYPE_POMODORO)
    val timerType = _timerType.asStateFlow()

    private var timerJob: Job? = null

    val pomodoroDurationMinutes: StateFlow<Int> = prefsRepository.pomodoroDuration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_POMODORO_MINUTES)

    val breakDurationMinutes: StateFlow<Int> = prefsRepository.breakDuration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_BREAK_MINUTES)

    val longBreakDurationMinutes: StateFlow<Int> = prefsRepository.longBreakDuration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_LONG_BREAK_MINUTES)

    val pomodoroCount: StateFlow<Int> = prefsRepository.pomodoroCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_POMODORO_COUNT)

    private val longBreakInterval: StateFlow<Int> = prefsRepository.longBreakInterval
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_LONG_BREAK_INTERVAL)

    private val autoStartPomodoros: StateFlow<Boolean> = prefsRepository.autoStartPomodoros
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_AUTO_START_POMODOROS)

    private val autoStartBreaks: StateFlow<Boolean> = prefsRepository.autoStartBreaks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_AUTO_START_BREAKS)

    init {
        viewModelScope.launch {
            resetTimerForCurrentType()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }

    fun startTimer() {
        if (_timerState.value == TIMER_STATE_IDLE) {
            viewModelScope.launch {
                if (_remainingTimeSeconds.value <= 0) {
                    resetTimerForCurrentType()
                }
                if (_remainingTimeSeconds.value > 0) {
                    startCountdown()
                }
            }
        }
    }

    fun pauseTimer() {
        if (_timerState.value == TIMER_STATE_RUNNING) {
            timerJob?.cancel()
            _timerState.value = TIMER_STATE_PAUSED
        }
    }

    fun resumeTimer() {
        if (_timerState.value == TIMER_STATE_PAUSED) {
            viewModelScope.launch {
                startCountdown()
            }
        }
    }

    fun skipForward() {
        if (_timerState.value == TIMER_STATE_RUNNING || _timerState.value == TIMER_STATE_PAUSED) {
            timerJob?.cancel()
            moveToNextTimer()
        }
    }

    fun restartTimer() {
        if (_timerState.value == TIMER_STATE_RUNNING || _timerState.value == TIMER_STATE_PAUSED) {
            timerJob?.cancel()
            viewModelScope.launch {
                resetTimerForCurrentType()
                _timerState.value = TIMER_STATE_IDLE
            }
        }
    }

    private fun startCountdown() {
        _timerState.value = TIMER_STATE_RUNNING

        timerJob = viewModelScope.launch {
            while (_remainingTimeSeconds.value != 0) {
                _remainingTimeSeconds.update { seconds ->
                    seconds - 1
                }
                delay(1000)
            }
            moveToNextTimer()
        }
    }

    private fun moveToNextTimer() {
        viewModelScope.launch {
            if (_timerType.value == TIMER_TYPE_POMODORO) {
                // Increment Active task completed pomodoro session by 1
                if (activeTask.value is UiState.Success && activeTask.value.data != null) {
                    updateActiveTaskSession()
                }

                // Decide whether the next type is long break or normal break
                _timerType.value = if (pomodoroCount.value % longBreakInterval.value == 0) TIMER_TYPE_LONG_BREAK else TIMER_TYPE_BREAK
                resetTimerForCurrentType()

                // Decide how to start the break (manually or automatically)
                if (autoStartBreaks.value) {
                    delay(1000)
                    startCountdown()
                } else {
                    _timerState.value = TIMER_STATE_IDLE
                }

                // Increment Pomodoro Count for today by 1
                prefsRepository.setPomodoroCount(pomodoroCount.value + 1)
            }
            // Timer type breaks (normal break or long break)
            else {
                if (
                    activeTask.value is UiState.Success &&
                    activeTask.value.data != null &&
                    activeTask.value.data?.completedSessions == activeTask.value.data?.pomodoroSessions
                ) {
                    setNextActiveTask()
                }

                _timerType.value = TIMER_TYPE_POMODORO
                resetTimerForCurrentType()

                if (autoStartPomodoros.value) {
                    delay(1000)
                    startCountdown()
                } else {
                    _timerState.value = TIMER_STATE_IDLE
                }
            }
        }
    }

    private fun resetTimerForCurrentType() {
        val durationSeconds = getDurationSecondsForType(_timerType.value)
        _remainingTimeSeconds.value = durationSeconds
    }

    private fun getDurationSecondsForType(type: String): Int {
        val minutes = when (type) {
            TIMER_TYPE_POMODORO -> pomodoroDurationMinutes.value
            TIMER_TYPE_BREAK -> breakDurationMinutes.value
            else -> longBreakDurationMinutes.value
        }
        return minutes * 60
    }

    private fun setNextActiveTask() {
        viewModelScope.launch {
            if (nextUncompletedTask.value is UiState.Success && nextUncompletedTask.value.data != null) {
                prefsRepository.setActiveTaskId((nextUncompletedTask.value.data?.id ?: 0).toLong())
            } else {
                prefsRepository.setActiveTaskId(0L)
            }
        }
    }

    private fun updateActiveTaskSession() {
        viewModelScope.launch {
            if (activeTask.value is UiState.Success && activeTask.value.data != null) {
                val updatedTask = activeTask.value.data!!.copy(
                    completedSessions = activeTask.value.data!!.completedSessions + 1
                )
                taskRepository.upsertTask(updatedTask).collect()
            }
        }
    }
}