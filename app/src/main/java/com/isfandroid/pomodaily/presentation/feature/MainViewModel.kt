package com.isfandroid.pomodaily.presentation.feature

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.resource.Result
import com.isfandroid.pomodaily.data.source.repository.PrefsRepository
import com.isfandroid.pomodaily.data.source.repository.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefsRepository: PrefsRepository,
    private val taskRepository: TaskRepository
): ViewModel() {

    fun initActiveTaskForToday() {
        viewModelScope.launch {
            val activeTaskId = prefsRepository.activeTaskId.first()
            if (activeTaskId == 0L) {
                taskRepository.getUncompletedTaskByDay(Calendar.getInstance().get(Calendar.DAY_OF_WEEK)).collect {
                    if (it is Result.Success && it.data != null) {
                        prefsRepository.setActiveTaskId((it.data.id ?: 0).toLong())
                    }
                }
            }
        }
    }
}