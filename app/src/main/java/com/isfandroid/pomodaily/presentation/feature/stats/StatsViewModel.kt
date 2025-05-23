package com.isfandroid.pomodaily.presentation.feature.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.isfandroid.pomodaily.data.source.repository.TaskRepository
import com.isfandroid.pomodaily.presentation.model.StatsUiModel
import com.isfandroid.pomodaily.utils.Constant.STATE_IN_TIMEOUT_MS
import com.isfandroid.pomodaily.utils.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(taskRepository: TaskRepository): ViewModel() {

    val todayStats = combine(
        taskRepository.totalTasksCompletedToday,
        taskRepository.totalTasksCompletedYesterday,
        taskRepository.totalTasksByDayMapped,
    ) { totalCompletedTasks, totalPreviousCompletedTasks, totalTasksByDay->
        val totalTasks = calculateTotalTasksForPeriod(
            DateUtils.getTodayStartCalendar(),
            DateUtils.getTodayEndCalendar(),
            totalTasksByDay
        )
        val totalPreviousTasks = calculateTotalTasksForPeriod(
            DateUtils.getYesterdayStartCalendar(),
            DateUtils.getYesterdayEndCalendar(),
            totalTasksByDay
        )

        StatsUiModel(
            completedTasks = totalCompletedTasks,
            totalTasks = totalTasks,
            completionRate = (totalCompletedTasks.toDouble() / totalTasks.toDouble() * 100).toInt(),
            previousCompletedTasks = totalPreviousCompletedTasks,
            previousTotalTasks = totalPreviousTasks,
            previousCompletionRate = (totalPreviousCompletedTasks.toDouble() / totalPreviousTasks.toDouble() * 100).toInt()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS),
        initialValue = null
    )

    val thisWeekStats = combine(
        taskRepository.totalTasksCompletedThisWeek,
        taskRepository.totalTasksCompletedLastWeek,
        taskRepository.totalTasksByDayMapped,
    ) { totalCompletedTasks, totalPreviousCompletedTasks, totalTasksByDay ->
        val totalTasks = calculateTotalTasksForPeriod(
            DateUtils.getThisWeekStartCalendar(),
            DateUtils.getThisWeekEndCalendar(),
            totalTasksByDay
        )
        val totalPreviousTasks = calculateTotalTasksForPeriod(
            DateUtils.getLastWeekStartCalendar(),
            DateUtils.getLastWeekEndCalendar(),
            totalTasksByDay
        )

        StatsUiModel(
            completedTasks = totalCompletedTasks,
            totalTasks = totalTasks,
            completionRate = (totalCompletedTasks.toDouble() / totalTasks.toDouble() * 100).toInt(),
            previousCompletedTasks = totalPreviousCompletedTasks,
            previousTotalTasks = totalPreviousTasks,
            previousCompletionRate = (totalPreviousCompletedTasks.toDouble() / totalPreviousTasks.toDouble() * 100).toInt()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS),
        initialValue = null
    )

    val thisMonthStats = combine(
        taskRepository.totalTasksCompletedThisMonth,
        taskRepository.totalTasksCompletedLastMonth,
        taskRepository.totalTasksByDayMapped,
    ) { totalCompletedTasks, totalPreviousCompletedTasks, totalTasksByDay ->
        val totalTasks = calculateTotalTasksForPeriod(
            DateUtils.getThisMonthStartCalendar(),
            DateUtils.getThisMonthEndCalendar(),
            totalTasksByDay
        )
        val totalPreviousTasks = calculateTotalTasksForPeriod(
            DateUtils.getLastMonthStartCalendar(),
            DateUtils.getLastMonthEndCalendar(),
            totalTasksByDay
        )

        StatsUiModel(
            completedTasks = totalCompletedTasks,
            totalTasks = totalTasks,
            completionRate = (totalCompletedTasks.toDouble() / totalTasks.toDouble() * 100).toInt(),
            previousCompletedTasks = totalPreviousCompletedTasks,
            previousTotalTasks = totalPreviousTasks,
            previousCompletionRate = (totalPreviousCompletedTasks.toDouble() / totalPreviousTasks.toDouble() * 100).toInt()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(STATE_IN_TIMEOUT_MS),
        initialValue = null
    )

    private fun calculateTotalTasksForPeriod(
        startDate: Calendar,
        endDate: Calendar,
        totalTasksByDay: Map<Int, Int>
    ): Int {
        var totalTasks = 0
        val currentDate = startDate.clone() as Calendar // Clone to avoid modifying the original

        while (!currentDate.after(endDate)) {
            val day = currentDate.get(Calendar.DAY_OF_WEEK)
            totalTasks += totalTasksByDay[day] ?: 0
            currentDate.add(Calendar.DAY_OF_MONTH, 1)
        }
        return totalTasks
    }
}