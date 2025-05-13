package com.isfandroid.pomodaily.di

import com.isfandroid.pomodaily.data.source.local.LocalDataSource
import com.isfandroid.pomodaily.data.source.repository.PomodoroRepository
import com.isfandroid.pomodaily.data.source.repository.SettingsRepository
import com.isfandroid.pomodaily.data.source.repository.TaskRepository
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
    fun provideSettingsRepository(localDataSource: LocalDataSource) = SettingsRepository(localDataSource)

    @Provides
    @Singleton
    fun provideTaskRepository(localDataSource: LocalDataSource) = TaskRepository(localDataSource)

    @Provides
    @Singleton
    fun providePomodoroRepository(localDataSource: LocalDataSource) = PomodoroRepository(localDataSource)
}