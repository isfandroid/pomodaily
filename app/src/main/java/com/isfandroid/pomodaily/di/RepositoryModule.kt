package com.isfandroid.pomodaily.di

import com.isfandroid.pomodaily.data.source.local.LocalDataSource
import com.isfandroid.pomodaily.data.source.repository.PrefsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {

    @Provides
    @Singleton
    fun providePrefsRepository(localDataSource: LocalDataSource) = PrefsRepository(localDataSource)
}