package com.isfandroid.pomodaily.workers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.isfandroid.pomodaily.utils.Constant.DAILY_RESET_WORK
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkerScheduler {
    fun scheduleDailyResetWorker(context: Context) {
        val dailyResetRequest = PeriodicWorkRequestBuilder<DailyResetWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setInitialDelay(calculateDelayUntilMidnight(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DAILY_RESET_WORK,
            ExistingPeriodicWorkPolicy.KEEP,
            dailyResetRequest
        )
    }

    private fun calculateDelayUntilMidnight(): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            add(Calendar.DATE, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val nextMidnightMillis = calendar.timeInMillis
        val delayInMillis = nextMidnightMillis - System.currentTimeMillis()

        return if (delayInMillis > 0) delayInMillis else 0L
    }
}