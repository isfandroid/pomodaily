package com.isfandroid.pomodaily.utils

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

object DateUtils {

    private val timeZone: TimeZone = TimeZone.getDefault()
    private val locale: Locale = Locale.getDefault()

    val DAYS_OF_WEEK = listOf(
        mapOf("id" to Calendar.MONDAY, "name" to "Monday"),
        mapOf("id" to Calendar.TUESDAY, "name" to "Tuesday"),
        mapOf("id" to Calendar.WEDNESDAY, "name" to "Wednesday"),
        mapOf("id" to Calendar.THURSDAY, "name" to "Thursday"),
        mapOf("id" to Calendar.FRIDAY, "name" to "Friday"),
        mapOf("id" to Calendar.SATURDAY, "name" to "Saturday"),
        mapOf("id" to Calendar.SUNDAY, "name" to "Sunday"),
    )
    val CURRENT_DAY = getCalendarInstance().get(Calendar.DAY_OF_WEEK)

    fun getTodayStartMillis(): Long = getTodayStartCalendar().timeInMillis
    fun getTodayEndMillis(): Long = getTodayEndCalendar().timeInMillis
    fun getYesterdayStartMillis(): Long = getYesterdayStartCalendar().timeInMillis
    fun getYesterdayEndMillis(): Long = getYesterdayEndCalendar().timeInMillis

    fun getThisWeekStartMillis(): Long = getThisWeekStartCalendar().timeInMillis
    fun getThisWeekEndMillis(): Long = getThisWeekEndCalendar().timeInMillis
    fun getLastWeekStartMillis(): Long = getLastWeekStartCalendar().timeInMillis
    fun getLastWeekEndMillis(): Long = getLastWeekEndCalendar().timeInMillis

    fun getThisMonthStartMillis(): Long = getThisMonthStartCalendar().timeInMillis
    fun getThisMonthEndMillis(): Long = getThisMonthEndCalendar().timeInMillis
    fun getLastMonthStartMillis(): Long = getLastMonthStartCalendar().timeInMillis
    fun getLastMonthEndMillis(): Long = getLastMonthEndCalendar().timeInMillis

    fun getTodayStartCalendar() = getCalendarInstance().toStartOfDay()
    fun getTodayEndCalendar() = getCalendarInstance().toEndOfDay()

    fun getYesterdayStartCalendar() = getCalendarInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.toStartOfDay()
    fun getYesterdayEndCalendar() = getCalendarInstance().apply { add(Calendar.DAY_OF_MONTH, -1) }.toEndOfDay()

    fun getThisWeekStartCalendar() = getCalendarInstance().apply { set(Calendar.DAY_OF_WEEK, firstDayOfWeek) }.toStartOfDay()
    fun getThisWeekEndCalendar() = getCalendarInstance().apply {
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        add(Calendar.DAY_OF_MONTH, 6)
    }.toEndOfDay()

    fun getLastWeekStartCalendar() = getCalendarInstance().apply {
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        add(Calendar.WEEK_OF_YEAR, -1)
    }.toStartOfDay()
    fun getLastWeekEndCalendar() = getCalendarInstance().apply {
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
        add(Calendar.DAY_OF_MONTH, 6)
        add(Calendar.WEEK_OF_YEAR, -1)
    }.toEndOfDay()

    fun getThisMonthStartCalendar() = getCalendarInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }.toStartOfDay()
    fun getThisMonthEndCalendar() = getCalendarInstance().apply { set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH)) }.toEndOfDay()

    fun getLastMonthStartCalendar() = getCalendarInstance().apply {
        add(Calendar.MONTH, -1)
        set(Calendar.DAY_OF_MONTH, 1)
    }.toStartOfDay()
    fun getLastMonthEndCalendar() = getCalendarInstance().apply {
        add(Calendar.MONTH, -1)
        set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
    }.toEndOfDay()

    private fun getCalendarInstance(): Calendar {
        return Calendar.getInstance(timeZone, locale).apply { firstDayOfWeek = Calendar.MONDAY }
    }

    private fun Calendar.toStartOfDay(): Calendar {
        this.set(Calendar.HOUR_OF_DAY, 0)
        this.set(Calendar.MINUTE, 0)
        this.set(Calendar.SECOND, 0)
        this.set(Calendar.MILLISECOND, 0)
        return this
    }

    private fun Calendar.toEndOfDay(): Calendar {
        this.set(Calendar.HOUR_OF_DAY, 23)
        this.set(Calendar.MINUTE, 59)
        this.set(Calendar.SECOND, 59)
        this.set(Calendar.MILLISECOND, 999)
        return this
    }
}