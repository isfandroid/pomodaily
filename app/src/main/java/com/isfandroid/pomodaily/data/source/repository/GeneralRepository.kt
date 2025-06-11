package com.isfandroid.pomodaily.data.source.repository

import com.isfandroid.pomodaily.data.source.local.GeneralLocalDataSource
import kotlinx.coroutines.flow.Flow

class GeneralRepository (
    private val localDataSource: GeneralLocalDataSource
) {

    fun getIsOnBoardingDone() = localDataSource.getIsOnBoardingDone()
    suspend fun setIsOnBoardingDone(value: Boolean) = localDataSource.setIsOnBoardingDone(value)

    fun getLastResetDate(): Flow<Long> = localDataSource.getLastResetDate()
    suspend fun setLastResetDate(value: Long) = localDataSource.setLastResetDate(value)
}