package com.isfandroid.pomodaily.data.source.repository.general

import kotlinx.coroutines.flow.Flow

interface GeneralRepository {

    fun getIsOnBoardingDone(): Flow<Boolean>
    suspend fun setIsOnBoardingDone(value: Boolean)

    fun getLastResetDate(): Flow<Long>
    suspend fun setLastResetDate(value: Long)
}