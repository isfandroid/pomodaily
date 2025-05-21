package com.isfandroid.pomodaily.presentation.feature.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.resource.Result
import com.isfandroid.pomodaily.data.source.repository.SettingsRepository
import com.isfandroid.pomodaily.data.source.repository.TaskRepository
import com.isfandroid.pomodaily.presentation.model.TaskScheduleUiModel
import com.isfandroid.pomodaily.presentation.resource.UiState
import com.isfandroid.pomodaily.utils.Constant.CURRENT_DAY
import com.isfandroid.pomodaily.utils.Constant.STATE_IN_TIMEOUT_MS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    settingsRepository: SettingsRepository,
): ViewModel() {

    val isTasksEmpty = taskRepository.getTasksByDay(CURRENT_DAY)
        .map {
            it is Result.Success && it.data.isEmpty()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val todoTasks = taskRepository.activeTaskId
        .combine(settingsRepository.pomodoroDuration) { activeTaskId, pomodoroDuration ->
            Pair(activeTaskId, pomodoroDuration)
        }.flatMapLatest { (activeTaskId, pomodoroDuration) ->
            taskRepository.getTasksByDay(CURRENT_DAY).map {
                when (it) {
                    is Result.Success -> {
                        val tasks = it.data
                        val toDoTasks = tasks.filter { task ->
                            task.completedSessions < task.pomodoroSessions
                        }.map { task ->
                            TaskScheduleUiModel(
                                id = task.id ?: 0,
                                name = task.name.orEmpty(),
                                completedSessions = task.completedSessions,
                                pomodoroSessions = task.pomodoroSessions,
                                remainingTimeMinutes = (task.pomodoroSessions - task.completedSessions) * pomodoroDuration,
                                isActive = (task.id ?: 0) == activeTaskId.toInt()
                            )
                        }
                        UiState.Success(toDoTasks)
                    }
                    is Result.Error -> UiState.Error(it.message)
                }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), UiState.Loading())

    val doneTasks = taskRepository.getTasksByDay(CURRENT_DAY)
        .map {
            when (it) {
                is Result.Success -> {
                    val tasks = it.data
                    val doneTasks = tasks.filter { task ->
                        task.completedSessions == task.pomodoroSessions
                    }.map { task ->
                        TaskScheduleUiModel(
                            id = task.id ?: 0,
                            name = task.name.orEmpty(),
                            completedSessions = task.completedSessions,
                            pomodoroSessions = task.pomodoroSessions,
                            remainingTimeMinutes = 0,
                            isActive = false
                        )
                    }
                    UiState.Success(doneTasks)
                }
                is Result.Error -> UiState.Error(it.message)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), UiState.Loading())

    fun setActiveTask(taskId: Int?) {
        viewModelScope.launch {
            taskRepository.setActiveTask(taskId?.toLong()).collect()
        }
    }
}