package com.isfandroid.pomodaily.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.isfandroid.pomodaily.data.source.repository.PrefsRepository
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
    private val prefsRepository: PrefsRepository,
    private val taskRepository: TaskRepository,
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val dateNowMillis = Calendar.getInstance().timeInMillis
        val lastResetDate = prefsRepository.lastResetDate.first()

        if (lastResetDate == dateNowMillis) {
            Log.i("DailyResetWorker", "Daily reset already done for today.")
            return Result.success()
        }

        val resetResult = taskRepository.resetTasksCompletedSessionsForDay(CURRENT_DAY).first()
        if (resetResult !is com.isfandroid.pomodaily.data.resource.Result.Success) {
            Log.e("DailyResetWorker", "Failed to reset tasks sessions.")
            return Result.failure()
        }

        val activeTaskResult = taskRepository.getUncompletedTaskByDay(CURRENT_DAY).first()
        if (activeTaskResult !is com.isfandroid.pomodaily.data.resource.Result.Success) {
            Log.e("DailyResetWorker", "Failed to set active task.")
            return Result.failure()
        }

        val activeTaskId = activeTaskResult.data?.id?.toLong() ?: 0L
        prefsRepository.setActiveTaskId(activeTaskId)
        prefsRepository.setLastResetDate(dateNowMillis)

        return Result.success()
    }
}