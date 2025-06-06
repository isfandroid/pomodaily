package com.isfandroid.pomodaily.data.source.local.general

import kotlinx.coroutines.flow.Flow

interface GeneralLocalDataSource {

    fun getIsOnBoardingDone(): Flow<Boolean>
    suspend fun setIsOnBoardingDone(value: Boolean)

    fun getLastResetDate(): Flow<Long>
    suspend fun setLastResetDate(value: Long)
}