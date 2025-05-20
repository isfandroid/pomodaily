package com.isfandroid.pomodaily.utils

import java.util.Calendar

object Constant {
    // DATA - LOCAL - PREFS
    const val APP_PREFS = "APP_PREFS"
    const val PREFS_KEY_IS_ON_BOARDING_DONE = "PREFS_KEY_IS_ON_BOARDING_DONE"
    const val PREFS_KEY_POMODORO_COUNT = "PREFS_KEY_POMODORO_COUNT"
    const val PREFS_KEY_POMODORO_DURATION = "PREFS_KEY_POMODORO_DURATION"
    const val PREFS_KEY_BREAK_DURATION = "PREFS_KEY_BREAK_DURATION"
    const val PREFS_KEY_LONG_BREAK_DURATION = "PREFS_KEY_LONG_BREAK_DURATION"
    const val PREFS_KEY_LONG_BREAK_INTERVAL = "PREFS_KEY_LONG_BREAK_INTERVAL"
    const val PREFS_KEY_SETTINGS_AUTO_START_BREAKS = "PREFS_KEY_SETTINGS_AUTO_START_BREAKS"
    const val PREFS_KEY_SETTINGS_AUTO_START_POMODOROS = "PREFS_KEY_SETTINGS_AUTO_START_POMODOROS"
    const val PREFS_KEY_LAST_RESET_DATE = "PREFS_KEY_LAST_RESET_DATE"
    const val PREFS_KEY_ACTIVE_TASK_ID = "PREFS_KEY_ACTIVE_TASK_ID"
    const val PREFS_KEY_APP_THEME = "PREFS_KEY_APP_THEME"

    // DATA - LOCAL - PREFS - VALUES
    const val DEFAULT_POMODORO_MINUTES = 25
    const val DEFAULT_BREAK_MINUTES = 5
    const val DEFAULT_LONG_BREAK_MINUTES = 10
    const val DEFAULT_LONG_BREAK_INTERVAL = 4
    const val DEFAULT_POMODORO_COUNT = 0
    const val DEFAULT_AUTO_START_POMODOROS = true
    const val DEFAULT_AUTO_START_BREAKS = true
    const val APP_THEME_DARK = "APP_THEME_DARK"
    const val APP_THEME_LIGHT = "APP_THEME_LIGHT"

    // DATA - LOCAL - DATABASE
    const val DB_NAME = "pomodaily.db"

    // DATA - DAYS
    val DAYS_OF_WEEK = listOf(
        mapOf("id" to Calendar.SUNDAY, "name" to "Sunday"),
        mapOf("id" to Calendar.MONDAY, "name" to "Monday"),
        mapOf("id" to Calendar.TUESDAY, "name" to "Tuesday"),
        mapOf("id" to Calendar.WEDNESDAY, "name" to "Wednesday"),
        mapOf("id" to Calendar.THURSDAY, "name" to "Thursday"),
        mapOf("id" to Calendar.FRIDAY, "name" to "Friday"),
        mapOf("id" to Calendar.SATURDAY, "name" to "Saturday"),
    )
    val CURRENT_DAY = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

    // NAVIGATION
    const val NAV_DESTINATION_ON_BOARDING = "NAV_DESTINATION_ON_BOARDING"
    const val NAV_DESTINATION_POMODORO = "NAV_DESTINATION_POMODORO"

    // TIMER STATE
    const val TIMER_STATE_IDLE = "TIMER_STATE_IDLE"
    const val TIMER_STATE_RUNNING = "TIMER_STATE_RUNNING"
    const val TIMER_STATE_PAUSED = "TIMER_STATE_PAUSED"

    // TIMER TYPE
    const val TIMER_TYPE_POMODORO = "TIMER_TYPE_POMODORO"
    const val TIMER_TYPE_BREAK = "TIMER_TYPE_BREAK"
    const val TIMER_TYPE_LONG_BREAK = "TIMER_TYPE_LONG_BREAK"

    // OTHERS
    const val STATE_IN_TIMEOUT_MS = 5000L

    // WORKER
    const val DAILY_RESET_WORK = "DAILY_RESET_WORK"

    // NOTIFICATIONS
    const val POMODORO_NOTIFICATION_ID = 1
    const val POMODORO_CHANNEL_ID = "CHANNEL_01"
    const val POMODORO_CHANNEL_NAME = "POMODAILY_POMODORO_CHANNEL"
}