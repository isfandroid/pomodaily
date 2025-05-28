package com.isfandroid.pomodaily.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.isfandroid.pomodaily.data.source.repository.SettingsRepository
import com.isfandroid.pomodaily.data.source.repository.TaskRepository
import com.isfandroid.pomodaily.utils.DateUtils
import com.isfandroid.pomodaily.utils.DateUtils.CURRENT_DAY
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class DailyResetWorker @AssistedInject constructor (
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val settingsRepository: SettingsRepository,
    private val taskRepository: TaskRepository,
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val todayStartMillis = DateUtils.getTodayStartMillis()
        val lastResetStartMillis = settingsRepository.lastResetDate.first()

        if (lastResetStartMillis == todayStartMillis) {
            return Result.success()
        }

        val lastMonthStartMillis = DateUtils.getLastMonthStartMillis()
        val resetResult = taskRepository.resetTasksCompletedSessionsForDay(CURRENT_DAY).first()
        val deleteResult = taskRepository.deleteTaskCompletionLogsOlderThan(lastMonthStartMillis).first()

        if (
            resetResult !is com.isfandroid.pomodaily.data.resource.Result.Success ||
            deleteResult !is com.isfandroid.pomodaily.data.resource.Result.Success
        ) {
            return Result.failure()
        }

        settingsRepository.setLastResetDate(todayStartMillis)
        return Result.success()
    }
}