package com.isfandroid.pomodaily.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.isfandroid.pomodaily.data.source.repository.SettingsRepository
import com.isfandroid.pomodaily.data.source.repository.TaskRepository
import com.isfandroid.pomodaily.utils.Constant.CURRENT_DAY
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.Calendar

@HiltWorker
class DailyResetWorker @AssistedInject constructor (
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val settingsRepository: SettingsRepository,
    private val taskRepository: TaskRepository,
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val dateNowMillis = Calendar.getInstance().timeInMillis
        val lastResetDate = settingsRepository.lastResetDate.first()

        if (lastResetDate == dateNowMillis) {
            return Result.success()
        }

        val resetResult = taskRepository.resetTasksCompletedSessionsForDay(CURRENT_DAY).first()
        if (resetResult !is com.isfandroid.pomodaily.data.resource.Result.Success) {
            return Result.failure()
        }

        val activeTaskResult = taskRepository.getUncompletedTaskByDay(CURRENT_DAY).first()
        if (activeTaskResult !is com.isfandroid.pomodaily.data.resource.Result.Success) {
            return Result.failure()
        }

        settingsRepository.setLastResetDate(dateNowMillis)

        return Result.success()
    }
}