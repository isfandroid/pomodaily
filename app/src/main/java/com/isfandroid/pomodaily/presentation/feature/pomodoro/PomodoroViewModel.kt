package com.isfandroid.pomodaily.presentation.feature.pomodoro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.model.Task
import com.isfandroid.pomodaily.data.resource.Result
import com.isfandroid.pomodaily.data.source.repository.PrefsRepository
import com.isfandroid.pomodaily.data.source.repository.TaskRepository
import com.isfandroid.pomodaily.presentation.resource.UiState
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_BREAK_MINUTES
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_LONG_BREAK_INTERVAL
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_LONG_BREAK_MINUTES
import com.isfandroid.pomodaily.utils.Constant.DEFAULT_POMODORO_MINUTES
import com.isfandroid.pomodaily.utils.Constant.STATE_IN_TIMEOUT_MS
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_IDLE
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_PAUSED
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_RUNNING
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_BREAK
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_POMODORO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class PomodoroViewModel @Inject constructor(
    private val prefsRepository: PrefsRepository,
    private val taskRepository: TaskRepository,
): ViewModel() {

    private val _activeTask = MutableStateFlow<UiState<Task>?>(null)
    val activeTask = _activeTask.asStateFlow()

    private val _remainingTimeSeconds = MutableStateFlow(0)
    val remainingTimeSeconds = _remainingTimeSeconds.asStateFlow()

    private val _timerState = MutableStateFlow(TIMER_STATE_IDLE)
    val timerState = _timerState.asStateFlow()

    private val _timerType = MutableStateFlow(TIMER_TYPE_POMODORO)
    val timerType = _timerType.asStateFlow()

    private var timerJob: Job? = null

    private val pomodoroCount: StateFlow<Int> = prefsRepository.pomodoroCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), 0)

    private val pomodoroDurationMinutes: StateFlow<Int> = prefsRepository.pomodoroDuration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_POMODORO_MINUTES)

    private val breakDurationMinutes: StateFlow<Int> = prefsRepository.breakDuration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_BREAK_MINUTES)

    private val longBreakDurationMinutes: StateFlow<Int> = prefsRepository.longBreakDuration
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_LONG_BREAK_MINUTES)

    private val longBreakInterval: StateFlow<Int> = prefsRepository.longBreakInterval
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), DEFAULT_LONG_BREAK_INTERVAL)

    init {
        viewModelScope.launch {
            resetTimerForCurrentType()
            getActiveTask()
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
                updateActiveTaskSession()

                _timerType.value = TIMER_TYPE_BREAK
                resetTimerForCurrentType()

                val autoStartBreaks = prefsRepository.autoStartBreaks.first()
                if (autoStartBreaks) startCountdown() else _timerState.value = TIMER_STATE_IDLE
            } else {
                val activeTaskId = prefsRepository.activeTaskId.first()
                if (activeTaskId != 0L && _activeTask.value?.data!!.completedSessions == _activeTask.value?.data!!.pomodoroSessions) {
                    setNextActiveTask()
                }

                _timerType.value = TIMER_TYPE_POMODORO
                resetTimerForCurrentType()

                val autoStartPomodoros = prefsRepository.autoStartPomodoros.first()
                if (autoStartPomodoros) startCountdown() else _timerState.value = TIMER_STATE_IDLE
            }
        }
    }

    private suspend fun resetTimerForCurrentType() {
        val durationSeconds = getDurationSecondsForType(_timerType.value)
        _remainingTimeSeconds.value = durationSeconds
    }

    private suspend fun getDurationSecondsForType(type: String): Int {
        val minutes = when (type) {
            TIMER_TYPE_POMODORO -> prefsRepository.pomodoroDuration.first()
            TIMER_TYPE_BREAK -> prefsRepository.breakDuration.first()
            else -> prefsRepository.longBreakDuration.first()
        }
        return minutes * 60
    }

    private fun getActiveTask() {
        _activeTask.value = UiState.Loading()

        viewModelScope.launch {
            val activeTaskId = prefsRepository.activeTaskId.first()
            if (activeTaskId == 0L) {
                _activeTask.value = null
            } else {
                taskRepository.getTask(activeTaskId).collect {
                    when(it) {
                        is Result.Success -> _activeTask.value = UiState.Success(it.data!!)
                        is Result.Error -> _activeTask.value = UiState.Error(it.message.orEmpty())
                    }
                }
            }
        }
    }

    private fun setNextActiveTask() {
        viewModelScope.launch {
            taskRepository.getUncompletedTaskByDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)).collect {
                if (it is Result.Success) {
                    if (it.data == null) {
                        prefsRepository.setActiveTaskId(0L)
                        _activeTask.value = null
                    } else {
                        prefsRepository.setActiveTaskId((it.data.id ?: 0).toLong())
                        _activeTask.value = UiState.Success(it.data)
                    }
                }
            }
        }
        getActiveTask()
    }

    private fun updateActiveTaskSession() {
        viewModelScope.launch {
            val activeTask = _activeTask.value?.data
            if (activeTask != null) {
                val updatedTask = activeTask.copy(
                    completedSessions = activeTask.completedSessions + 1
                )
                taskRepository.upsertTask(updatedTask).collect {
                    if (it is Result.Success) getActiveTask()
                }
            }
        }
    }
}