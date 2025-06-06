package com.isfandroid.pomodaily.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.isfandroid.pomodaily.data.source.repository.general.GeneralRepository
import com.isfandroid.pomodaily.data.source.repository.task.TaskRepository
import com.isfandroid.pomodaily.utils.DateUtils
import com.isfandroid.pomodaily.utils.DateUtils.CURRENT_DAY
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class DailyResetWorker @AssistedInject constructor (
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val generalRepository: GeneralRepository,
    private val taskRepository: TaskRepository,
): CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val todayStartMillis = DateUtils.getTodayStartMillis()
        val lastResetStartMillis = generalRepository.getLastResetDate().first()

        if (lastResetStartMillis == todayStartMillis) {
            return Result.success()
        }

        val lastMonthStartMillis = DateUtils.getLastMonthStartMillis()
        taskRepository.deleteTaskCompletionLogsOlderThan(lastMonthStartMillis)

        taskRepository.resetTasksCompletedSessionsForDay(CURRENT_DAY)

        val nextActiveTask = taskRepository.getUncompletedTaskByDay(CURRENT_DAY).first()
        if (nextActiveTask != null) taskRepository.updateActiveTaskId(nextActiveTask.id ?: 0)

        generalRepository.setLastResetDate(todayStartMillis)
        return Result.success()
    }
}