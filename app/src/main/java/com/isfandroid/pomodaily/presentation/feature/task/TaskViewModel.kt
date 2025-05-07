package com.isfandroid.pomodaily.presentation.feature.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.model.Task
import com.isfandroid.pomodaily.data.resource.Result
import com.isfandroid.pomodaily.data.source.repository.TaskRepository
import com.isfandroid.pomodaily.presentation.model.ExpandableTaskUiModel
import com.isfandroid.pomodaily.presentation.resource.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository
): ViewModel() {

    private val _getTasksResult = MutableSharedFlow<UiState<List<Task>>>()
    val getTasksResult = _getTasksResult.asSharedFlow()

    private val _updateTaskResult = MutableSharedFlow<UiState<Unit>>()
    val updateTaskResult = _updateTaskResult.asSharedFlow()

    private val _deleteTaskResult = MutableSharedFlow<UiState<Unit>>()
    val deleteTaskResult = _deleteTaskResult.asSharedFlow()

    private val _selectedDayId = MutableStateFlow(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
    val selectedDayId = _selectedDayId.asStateFlow()

    private val _tasks = MutableStateFlow<List<ExpandableTaskUiModel>?>(null)
    val tasks = _tasks.asStateFlow()

    private val _currentlyExpandedTaskId = MutableStateFlow<Int?>(null)

    init {
        getTasks()
    }

    fun selectDay(id: Int) {
        _selectedDayId.value = id
        getTasks()
    }

    fun getTasks() {
        viewModelScope.launch {
            _getTasksResult.emit(UiState.Loading())

            taskRepository.getTasksByDay(_selectedDayId.value).collect {
                when(it) {
                    is Result.Success -> {
                        _getTasksResult.emit(UiState.Success(it.data.orEmpty()))

                        val tasksData = it.data?.map { task ->
                            ExpandableTaskUiModel (
                                task = task,
                                isExpanded = task.id == _currentlyExpandedTaskId.value
                            )
                        }
                        _tasks.value = tasksData
                    }
                    is Result.Error -> _getTasksResult.emit(UiState.Error(it.message.orEmpty()))
                }
            }
        }
    }

    fun addNewTaskEntry() {
        val taskEntry = ExpandableTaskUiModel(
            task = Task(
                dayOfWeek = _selectedDayId.value,
                order = null,
                name = null,
            ),
        )
        _tasks.update {
            it?.plus(taskEntry)
        }
        setExpandedTask(null)
    }

    fun deleteNewTaskEntry() {
        if (_tasks.value?.any { it.task.id == null } == true) {
            _tasks.update {
                it?.minus(it.first { task -> task.task.id == null })
            }
        }
        setExpandedTask(null)
    }

    fun updateTask(task: Task) {
        setExpandedTask(null)

        viewModelScope.launch {
            _updateTaskResult.emit(UiState.Loading())
            taskRepository.upsertTask(task).collect {
                when(it) {
                    is Result.Success -> {
                        _updateTaskResult.emit(UiState.Success(Unit))
                        getTasks()
                    }
                    is Result.Error -> _updateTaskResult.emit(UiState.Error(it.message.orEmpty()))
                }
            }
        }
    }

    fun deleteTask(task: Task) {
        setExpandedTask(null)

        viewModelScope.launch {
            _deleteTaskResult.emit(UiState.Loading())
            taskRepository.deleteTask(task).collect {
                when(it) {
                    is Result.Success -> {
                        _deleteTaskResult.emit(UiState.Success(Unit))
                        getTasks()
                    }
                    is Result.Error -> _deleteTaskResult.emit(UiState.Error(it.message.orEmpty()))
                }
            }
        }
    }

    fun setExpandedTask(id: Int?) {
        _currentlyExpandedTaskId.value = id

        _tasks.update {
            it?.map { task ->
                task.copy(isExpanded = task.task.id == _currentlyExpandedTaskId.value)
            }
        }
    }
}