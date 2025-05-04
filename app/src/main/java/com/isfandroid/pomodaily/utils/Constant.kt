package com.isfandroid.pomodaily.utils

object Constant {
    // DATA - LOCAL - PREFS
    const val APP_PREFS = "APP_PREFS"
    const val PREFS_KEY_IS_ON_BOARDING_DONE = "PREFS_KEY_IS_ON_BOARDING_DONE"
    const val PREFS_KEY_POMODORO_DURATION = "PREFS_KEY_POMODORO_DURATION"
    const val PREFS_KEY_BREAK_DURATION = "PREFS_KEY_BREAK_DURATION"
    const val PREFS_KEY_LONG_BREAK_DURATION = "PREFS_KEY_LONG_BREAK_DURATION"

    // DATA - LOCAL - DATABASE
    const val DB_NAME = "pomodaily.db"

    // DATA - DAY ID
    const val DAY_ID_MONDAY = 1
    const val DAY_ID_TUESDAY = 2
    const val DAY_ID_WEDNESDAY = 3
    const val DAY_ID_THURSDAY = 4
    const val DAY_ID_FRIDAY = 5
    const val DAY_ID_SATURDAY = 6
    const val DAY_ID_SUNDAY = 7

    // DATA - DAYS
    val DAYS_OF_WEEK = listOf(
        mapOf("id" to DAY_ID_MONDAY, "name" to "Monday"),
        mapOf("id" to DAY_ID_TUESDAY, "name" to "Tuesday"),
        mapOf("id" to DAY_ID_WEDNESDAY, "name" to "Wednesday"),
        mapOf("id" to DAY_ID_THURSDAY, "name" to "Thursday"),
        mapOf("id" to DAY_ID_FRIDAY, "name" to "Friday"),
        mapOf("id" to DAY_ID_SATURDAY, "name" to "Saturday"),
        mapOf("id" to DAY_ID_SUNDAY, "name" to "Sunday")
    )

    // NAVIGATION
    const val NAV_DESTINATION_ON_BOARDING = "NAV_DESTINATION_ON_BOARDING"
    const val NAV_DESTINATION_SCHEDULE = "NAV_DESTINATION_SCHEDULE"
    const val NAV_DESTINATION_TASKS = "NAV_DESTINATION_TASKS"
    const val NAV_DESTINATION_POMODORO = "NAV_DESTINATION_POMODORO"
}