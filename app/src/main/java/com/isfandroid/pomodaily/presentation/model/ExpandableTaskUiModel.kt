package com.isfandroid.pomodaily.presentation.model

data class ExpandableTaskUiModel(
    val id: Int?,
    val dayOfWeek: Int?,
    val order: Int?,
    val name: String?,
    val completedSessions: Int?,
    val pomodoroSessions: Int?,
    val note: String? = null,
    var isExpanded: Boolean = false,
    var isNewEntry: Boolean = false
)