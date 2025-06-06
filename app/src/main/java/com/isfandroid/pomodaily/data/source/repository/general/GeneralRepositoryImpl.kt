package com.isfandroid.pomodaily.data.source.repository.general

import com.isfandroid.pomodaily.data.source.local.general.GeneralLocalDataSource
import kotlinx.coroutines.flow.Flow

class GeneralRepositoryImpl (
    private val localDataSource: GeneralLocalDataSource
): GeneralRepository {

    override fun getIsOnBoardingDone() = localDataSource.getIsOnBoardingDone()
    override suspend fun setIsOnBoardingDone(value: Boolean) = localDataSource.setIsOnBoardingDone(value)

    override fun getLastResetDate(): Flow<Long> = localDataSource.getLastResetDate()
    override suspend fun setLastResetDate(value: Long) = localDataSource.setLastResetDate(value)
}