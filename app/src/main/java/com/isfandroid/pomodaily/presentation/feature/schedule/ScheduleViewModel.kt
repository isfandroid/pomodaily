package com.isfandroid.pomodaily.presentation.feature.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.source.repository.pomodoro.PomodoroRepository
import com.isfandroid.pomodaily.data.source.repository.task.TaskRepository
import com.isfandroid.pomodaily.presentation.model.TaskScheduleUiModel
import com.isfandroid.pomodaily.utils.Constant.STATE_IN_TIMEOUT_MS
import com.isfandroid.pomodaily.utils.DateUtils.CURRENT_DAY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    pomodoroRepository: PomodoroRepository,
): ViewModel() {

    val isTasksEmpty = taskRepository.getTasksByDay(CURRENT_DAY)
        .map { it.isEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val todoTasks =
        combine(
            taskRepository.getActiveTask(),
            pomodoroRepository.getPomodoroDuration()
        ) { activeTask, pomodoroDuration ->
            Pair(activeTask, pomodoroDuration)
        }
        .flatMapLatest { (activeTask, pomodoroDuration) ->
            taskRepository.getTasksByDay(CURRENT_DAY).map { tasks ->
                tasks.filter { task ->
                    task.completedSessions < task.pomodoroSessions
                }.map { task ->
                    TaskScheduleUiModel(
                        id = task.id ?: 0,
                        name = task.name.orEmpty(),
                        completedSessions = task.completedSessions,
                        pomodoroSessions = task.pomodoroSessions,
                        remainingTimeMinutes = (task.pomodoroSessions - task.completedSessions) * pomodoroDuration,
                        isActive = (task.id ?: 0) == activeTask?.id
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), emptyList())

    val doneTasks = taskRepository.getTasksByDay(CURRENT_DAY)
        .map { tasks ->
            tasks.filter { task ->
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
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS), emptyList())

    fun setActiveTask(taskId: Int?) {
        viewModelScope.launch {
            taskRepository.updateActiveTaskId(taskId ?: 0)
        }
    }
}