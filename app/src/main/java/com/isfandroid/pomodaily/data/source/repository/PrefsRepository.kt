package com.isfandroid.pomodaily.data.source.repository

import com.isfandroid.pomodaily.data.source.local.LocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrefsRepository @Inject constructor(
    private val localDataSource: LocalDataSource
) {

    val isOnBoardingDone = localDataSource.isOnBoardingDone
    suspend fun setIsOnBoardingDone(value: Boolean) {
        localDataSource.setIsOnBoardingDone(value)
    }
}