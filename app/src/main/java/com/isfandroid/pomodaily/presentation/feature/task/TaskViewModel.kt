package com.isfandroid.pomodaily.presentation.feature.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.source.repository.task.TaskRepository
import com.isfandroid.pomodaily.presentation.model.ExpandableTaskUiModel
import com.isfandroid.pomodaily.utils.Constant.STATE_IN_TIMEOUT_MS
import com.isfandroid.pomodaily.utils.DataMapper.mapDomainTaskToExpandableTaskUiModel
import com.isfandroid.pomodaily.utils.DataMapper.mapExpandableTaskUiModelToDomain
import com.isfandroid.pomodaily.utils.DateUtils.CURRENT_DAY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
): ViewModel() {

    private val _selectedDayId = MutableStateFlow(CURRENT_DAY)
    val selectedDayId = _selectedDayId.asStateFlow()

    private val _newTaskEntry = MutableStateFlow<ExpandableTaskUiModel?>(null)
    private val _expandedTaskId = MutableStateFlow<Int?>(null)

    val tasks = combine(
        _selectedDayId,
        _newTaskEntry,
        _expandedTaskId,
    ) { dayId, newTaskEntry, expandedTaskId ->
        Triple(dayId, newTaskEntry, expandedTaskId)
    }
        .flatMapLatest { (dayId, newTaskEntry, expandedTaskId) ->
            taskRepository.getTasksByDay(dayId).map { tasks ->
                val savedTasks = tasks.map {
                    mapDomainTaskToExpandableTaskUiModel(it).copy(
                        isExpanded = expandedTaskId == it.id,
                    )
                }
                newTaskEntry?.let { savedTasks + it } ?: savedTasks
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS),
            initialValue = emptyList()
        )

    val daysWithTasks = taskRepository.getDaysWithTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS),
            initialValue = emptyList()
        )

    private val _updateTaskResult = MutableSharedFlow<Boolean>()
    val updateTaskResult =_updateTaskResult.asSharedFlow()

    private val _deleteTaskResult = MutableSharedFlow<Boolean>()
    val deleteTaskResult =_deleteTaskResult.asSharedFlow()

    private val _copyTasksResult = MutableSharedFlow<Boolean>()
    val copyTasksResult =_copyTasksResult.asSharedFlow()

    fun selectDay(dayId: Int) {
        _selectedDayId.value = dayId
    }

    fun addNewTaskEntry() {
        _newTaskEntry.value = ExpandableTaskUiModel(
            id = 0,
            dayOfWeek = _selectedDayId.value,
            order = null,
            name = null,
            pomodoroSessions = 1,
            completedSessions = 0,
            note = null,
            isExpanded = true,
            isNewEntry = true,
        )
        setExpandedTaskId(0)
    }

    fun deleteNewTaskEntry() {
        _newTaskEntry.value = null
    }

    fun setExpandedTaskId(taskId: Int?) {
        _expandedTaskId.value = taskId
    }

    fun insertTask(task: ExpandableTaskUiModel) {
        viewModelScope.launch {
            val domainTask = mapExpandableTaskUiModelToDomain(task)
            taskRepository.insertTask(domainTask)
            _updateTaskResult.emit(true)
        }
    }

    fun updateTask(task: ExpandableTaskUiModel) {
        viewModelScope.launch {
            val domainTask = mapExpandableTaskUiModelToDomain(task)
            taskRepository.updateTask(domainTask)
            _updateTaskResult.emit(true)
        }
    }

    fun deleteTask(task: ExpandableTaskUiModel) {
        viewModelScope.launch {
            val domainTask = mapExpandableTaskUiModelToDomain(task)
            taskRepository.deleteTask(domainTask.id ?: 0)
            _deleteTaskResult.emit(true)
        }
    }

    fun copyTasks(fromDay: Int, toDay: Int) {
        viewModelScope.launch {
            taskRepository.copyTasks(fromDay, toDay)
            _copyTasksResult.emit(true)
        }
    }
}