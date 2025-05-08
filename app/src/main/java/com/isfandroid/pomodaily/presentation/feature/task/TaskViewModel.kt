package com.isfandroid.pomodaily.presentation.feature.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.resource.Result
import com.isfandroid.pomodaily.data.source.repository.TaskRepository
import com.isfandroid.pomodaily.presentation.model.ExpandableTaskUiModel
import com.isfandroid.pomodaily.presentation.resource.UiState
import com.isfandroid.pomodaily.utils.Constant.CURRENT_DAY
import com.isfandroid.pomodaily.utils.DataMapper.mapDomainTaskToExpandableTaskUiModel
import com.isfandroid.pomodaily.utils.DataMapper.mapExpandableTaskUiModelToDomain
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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

    private val _newTaskEntry = MutableStateFlow<ExpandableTaskUiModel?>(null)
    private val _expandedTaskId = MutableStateFlow<Int?>(null)
    private val _refreshTrigger = MutableStateFlow(Unit)

    private val _selectedDayId = MutableStateFlow(CURRENT_DAY)
    val selectedDayId = _selectedDayId.asStateFlow()

    private val _updateTaskResult = MutableSharedFlow<UiState<Unit>>()
    val updateTaskResult =_updateTaskResult.asSharedFlow()

    private val _deleteTaskResult = MutableSharedFlow<UiState<Unit>>()
    val deleteTaskResult =_deleteTaskResult.asSharedFlow()

    val tasks: StateFlow<UiState<List<ExpandableTaskUiModel>>> =
        _selectedDayId.combine(_newTaskEntry) { dayId, newTask ->
            Pair(dayId, newTask)
        }.combine(_expandedTaskId) { (dayId, newTask), expandedId ->
            Triple(dayId, newTask, expandedId)
        }.combine(_refreshTrigger) { (dayId, newTask, expandedId), _ ->
            Triple(dayId, newTask, expandedId)
        }.flatMapLatest { (dayId, newTask, expandedId) ->
            taskRepository.getTasksByDay(dayId).map { result ->
                when (result) {
                    is Result.Success -> {
                        val savedTasks = result.data.map {
                            mapDomainTaskToExpandableTaskUiModel(it).copy(
                                isExpanded = expandedId == it.id,
                            )
                        }
                        val combinedTasks = newTask?.let { savedTasks + it } ?: savedTasks
                        UiState.Success(combinedTasks)
                    }
                    is Result.Error -> UiState.Error(result.message)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = UiState.Loading()
        )

    fun selectDay(dayId: Int) {
        _selectedDayId.value = dayId
    }

    fun refreshTasks() {
        _refreshTrigger.value = Unit
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

    fun updateTask(task: ExpandableTaskUiModel) {
        viewModelScope.launch {
            val domainTask = mapExpandableTaskUiModelToDomain(task)
            taskRepository.upsertTask(domainTask).collect {
                when(it) {
                    is Result.Success -> _updateTaskResult.emit(UiState.Success(Unit))
                    is Result.Error -> _updateTaskResult.emit(UiState.Error(it.message))
                }
            }
        }
    }

    fun deleteTask(task: ExpandableTaskUiModel) {
        viewModelScope.launch {
            val domainTask = mapExpandableTaskUiModelToDomain(task)
            taskRepository.deleteTask(domainTask).collect {
                when(it) {
                    is Result.Success -> _deleteTaskResult.emit(UiState.Success(Unit))
                    is Result.Error -> _deleteTaskResult.emit(UiState.Error(it.message))
                }
            }
        }
    }
}