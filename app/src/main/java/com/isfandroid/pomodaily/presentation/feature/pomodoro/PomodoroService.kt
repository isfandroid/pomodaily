package com.isfandroid.pomodaily.presentation.feature.pomodoro

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import androidx.core.app.NotificationManagerCompat
import com.isfandroid.pomodaily.R
import com.isfandroid.pomodaily.data.model.TaskCompletionLog
import com.isfandroid.pomodaily.data.model.TimerData
import com.isfandroid.pomodaily.data.source.repository.PomodoroRepository
import com.isfandroid.pomodaily.data.source.repository.SettingsRepository
import com.isfandroid.pomodaily.data.source.repository.TaskRepository
import com.isfandroid.pomodaily.presentation.feature.MainActivity
import com.isfandroid.pomodaily.utils.Constant.POMODORO_ALERT_CHANNEL_ID
import com.isfandroid.pomodaily.utils.Constant.POMODORO_ALERT_CHANNEL_NAME
import com.isfandroid.pomodaily.utils.Constant.POMODORO_TIMER_CHANNEL_ID
import com.isfandroid.pomodaily.utils.Constant.POMODORO_TIMER_CHANNEL_NAME
import com.isfandroid.pomodaily.utils.Constant.POMODORO_ALERT_NOTIFICATION_ID
import com.isfandroid.pomodaily.utils.Constant.POMODORO_TIMER_NOTIFICATION_ID
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_IDLE
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_PAUSED
import com.isfandroid.pomodaily.utils.Constant.TIMER_STATE_RUNNING
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_BREAK
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_LONG_BREAK
import com.isfandroid.pomodaily.utils.Constant.TIMER_TYPE_POMODORO
import com.isfandroid.pomodaily.utils.DateUtils.CURRENT_DAY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class PomodoroService: Service() {

    @Inject lateinit var pomodoroRepository: PomodoroRepository
    @Inject lateinit var settingsRepository: SettingsRepository
    @Inject lateinit var taskRepository: TaskRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var stateObserverJob: Job? = null
    private var timerJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        initObserver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startTimer()
            ACTION_PAUSE -> pauseTimer()
            ACTION_RESUME -> resumeTimer()
            ACTION_RESTART -> restartTimer()
            ACTION_SKIP -> finishTimer()
            else -> {}
        }

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        timerJob?.cancel()
        stateObserverJob?.cancel()
        serviceScope.cancel()
    }

    private fun initObserver() {
        stateObserverJob?.cancel()
        stateObserverJob = serviceScope.launch {
            pomodoroRepository.timerData.collect {
                if (it.state != TIMER_STATE_IDLE) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        startForeground(POMODORO_TIMER_NOTIFICATION_ID, createNotification(it), ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
                    } else {
                        startForeground(POMODORO_TIMER_NOTIFICATION_ID, createNotification(it))
                    }
                }
            }
        }
    }

    private fun createNotification(timerData: TimerData): Notification {
        val pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            action = MainActivity.ACTION_NAVIGATE_TO_POMODORO
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            pendingIntentFlags
        )

        val remainingTime = getString(R.string.txt_value_timer, timerData.remainingTime/60, timerData.remainingTime%60)
        val message = when (timerData.state) {
            TIMER_STATE_RUNNING -> {
                if (timerData.type == TIMER_TYPE_POMODORO) {
                    getString(R.string.txt_stay_focus)
                } else {
                    getString(R.string.txt_take_a_break)
                }
            }
            TIMER_STATE_PAUSED -> getString(R.string.txt_paused)
            else -> getString(R.string.txt_stopped)
        }

        val notificationLayout = RemoteViews(packageName, R.layout.notification_timer).apply {
            setTextViewText(R.id.tv_timer, remainingTime)
            setTextViewText(R.id.tv_message, message)
            setOnClickPendingIntent(R.id.btn_restart, createActionPendingIntent(ACTION_RESTART))
            setOnClickPendingIntent(R.id.btn_start, createActionPendingIntent(ACTION_START))
            setOnClickPendingIntent(R.id.btn_pause, createActionPendingIntent(ACTION_PAUSE))
            setOnClickPendingIntent(R.id.btn_resume, createActionPendingIntent(ACTION_RESUME))
            setOnClickPendingIntent(R.id.btn_skip, createActionPendingIntent(ACTION_SKIP))

            when (timerData.state) {
                TIMER_STATE_RUNNING -> {
                    setViewVisibility(R.id.btn_restart, View.VISIBLE)
                    setViewVisibility(R.id.btn_skip, View.VISIBLE)
                    setViewVisibility(R.id.btn_start, View.GONE)
                    setViewVisibility(R.id.btn_pause, View.VISIBLE)
                    setViewVisibility(R.id.btn_resume, View.GONE)
                }
                TIMER_STATE_PAUSED -> {
                    setViewVisibility(R.id.btn_restart, View.VISIBLE)
                    setViewVisibility(R.id.btn_skip, View.VISIBLE)
                    setViewVisibility(R.id.btn_start, View.GONE)
                    setViewVisibility(R.id.btn_pause, View.GONE)
                    setViewVisibility(R.id.btn_resume, View.VISIBLE)
                }
                else -> {}
            }
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(this, POMODORO_TIMER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_app_logo)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setCustomBigContentView(notificationLayout)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(timerData.state == TIMER_STATE_RUNNING)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            notificationBuilder.setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                POMODORO_TIMER_CHANNEL_ID,
                POMODORO_TIMER_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = POMODORO_TIMER_CHANNEL_NAME
            }
            notificationBuilder.setChannelId(POMODORO_TIMER_CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        return notificationBuilder.build()
    }

    private fun createActionPendingIntent(action: String): PendingIntent {
        val intent = Intent(this, PomodoroService::class.java).apply { this.action = action }
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val requestCode = when(action) {
            ACTION_START -> 1
            ACTION_PAUSE -> 2
            ACTION_RESUME -> 3
            ACTION_RESTART -> 4
            ACTION_SKIP -> 5
            else -> 0
        }
        return PendingIntent.getService(this, requestCode, intent, flags)
    }

    private fun startTimer() {
        serviceScope.launch {
            val timerData = pomodoroRepository.timerData.first()
            if (timerData.state == TIMER_STATE_IDLE) {
                if (timerData.remainingTime <= 0) pomodoroRepository.resetTimerForCurrentType() else startCountdown()
            }
        }
    }

    private fun pauseTimer() {
        serviceScope.launch {
            val timerData = pomodoroRepository.timerData.first()
            if (timerData.state == TIMER_STATE_RUNNING) {
                timerJob?.cancel()
                pomodoroRepository.setTimerState(TIMER_STATE_PAUSED)
            }
        }
    }

    private fun resumeTimer() {
        serviceScope.launch {
            val timerData = pomodoroRepository.timerData.first()
            if (timerData.state == TIMER_STATE_PAUSED) startCountdown()
        }
    }

    private fun restartTimer() {
        serviceScope.launch {
            val timerData = pomodoroRepository.timerData.first()
            if (timerData.state != TIMER_STATE_IDLE) {
                timerJob?.cancel()
                stateObserverJob?.cancel()

                pomodoroRepository.resetTimerForCurrentType()
                pomodoroRepository.setTimerState(TIMER_STATE_IDLE)

                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    private fun finishTimer() {
        serviceScope.launch {
            val timerData = pomodoroRepository.timerData.first()
            if (timerData.state != TIMER_STATE_IDLE) {
                timerJob?.cancel()
                moveToNextTimer()
            }
        }
    }

    private fun startCountdown() {
        timerJob?.cancel()
        pomodoroRepository.setTimerState(TIMER_STATE_RUNNING)

        timerJob = serviceScope.launch {
            var remainingTime = pomodoroRepository.timerData.first().remainingTime
            while(remainingTime != 0) {
                delay(1000)
                remainingTime--
                pomodoroRepository.updateRemainingTime(remainingTime)
            }
            moveToNextTimer()
        }
    }

    private fun moveToNextTimer() {
        serviceScope.launch {
            val timerType = pomodoroRepository.timerData.first().type
            val pomodoroCount = settingsRepository.pomodoroCount.first()
            val longBreakInterval = settingsRepository.longBreakInterval.first()
            val autoStartPomodoros = settingsRepository.autoStartPomodoros.first()
            val autoStartBreaks = settingsRepository.autoStartBreaks.first()
            val activeTask = taskRepository.activeTask.first()
            val uncompletedTask = taskRepository.getUncompletedTaskByDay(CURRENT_DAY).first()

            if (timerType == TIMER_TYPE_POMODORO) {
                // Increment pomodoro count by 1
                settingsRepository.setPomodoroCount(pomodoroCount + 1)

                // Increment Active Task (if any) completed sessions
                if (activeTask != null) {
                    val updatedTask = activeTask.copy(
                        completedSessions = activeTask.completedSessions + 1
                    )
                    taskRepository.upsertTask(updatedTask).collect()
                }

                // Set next break type & timer
                if (pomodoroCount > 0 && pomodoroCount % longBreakInterval == 0) {
                    pomodoroRepository.setTimerType(TIMER_TYPE_LONG_BREAK)
                } else {
                    pomodoroRepository.setTimerType(TIMER_TYPE_BREAK)
                }
                pomodoroRepository.resetTimerForCurrentType()

                // Set how to start/stop the break
                if (autoStartBreaks) {
                    startCountdown()
                } else {
                    pomodoroRepository.setTimerState(TIMER_STATE_IDLE)
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }

                showTimerFinishedAlertNotification(
                    title = "Pomodoro Finished",
                    message = "Take a Break"
                )
            } else {
                // Set type to pomodoro & reset timer
                pomodoroRepository.setTimerType(TIMER_TYPE_POMODORO)
                pomodoroRepository.resetTimerForCurrentType()

                if (activeTask != null) {
                    // Add new completion log entry if active task is completed
                    if (activeTask.completedSessions == activeTask.pomodoroSessions) {
                        taskRepository.insertTaskCompletionLog(
                            TaskCompletionLog(
                                taskId = (activeTask.id ?: 0).toLong(),
                                completionDate = Calendar.getInstance().timeInMillis
                            )
                        ).collect()
                    }

                    // Set next active task (if theres any uncompleted session)
                    if (uncompletedTask != null) {
                        taskRepository.setActiveTask((uncompletedTask.id ?: 0).toLong()).collect()
                    } else {
                        taskRepository.setActiveTask(null).collect()
                    }
                }

                // Set how to start/stop pomodoro
                if (autoStartPomodoros) {
                    startCountdown()
                } else {
                    pomodoroRepository.setTimerState(TIMER_STATE_IDLE)
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }

                showTimerFinishedAlertNotification(
                    title = "Break Finished",
                    message = "Back to Focus"
                )
            }
        }
    }

    private fun showTimerFinishedAlertNotification(title: String, message: String) {
        val pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        val notificationIntent = Intent(this, MainActivity::class.java).apply {
            action = MainActivity.ACTION_NAVIGATE_TO_POMODORO
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            pendingIntentFlags
        )

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(this, POMODORO_ALERT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_app_logo)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            val mVibrationPattern = longArrayOf(0, 500, 200, 500)

            val channel = NotificationChannel(
                POMODORO_ALERT_CHANNEL_ID,
                POMODORO_ALERT_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = POMODORO_ALERT_CHANNEL_NAME
                setSound(soundUri, audioAttributes)
                enableVibration(true)
                vibrationPattern = mVibrationPattern
            }
            notificationBuilder.setChannelId(POMODORO_ALERT_CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)
        }

        try {
            NotificationManagerCompat.from(this).notify(POMODORO_ALERT_NOTIFICATION_ID, notificationBuilder.build())
        } catch (e: SecurityException) {
            Log.e("PomodoroApp", "Permission issue showing alert notification.", e)
        }

        Handler(Looper.getMainLooper()).postDelayed(
            { NotificationManagerCompat.from(this).cancel(POMODORO_ALERT_NOTIFICATION_ID)
            }, 5000)
    }

    companion object {
        const val ACTION_START = "POMODORO_SERVICE_ACTION_START"
        const val ACTION_PAUSE = "POMODORO_SERVICE_ACTION_PAUSE"
        const val ACTION_RESUME = "POMODORO_SERVICE_ACTION_RESUME"
        const val ACTION_RESTART = "POMODORO_SERVICE_ACTION_RESTART"
        const val ACTION_SKIP = "POMODORO_SERVICE_ACTION_SKIP"

        fun createControlIntent(context: Context, action: String): Intent {
            return Intent(context, PomodoroService::class.java).apply {
                this.action = action
            }
        }
    }
}