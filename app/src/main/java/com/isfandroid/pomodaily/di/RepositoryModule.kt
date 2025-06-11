package com.isfandroid.pomodaily.di

import com.isfandroid.pomodaily.data.source.local.GeneralLocalDataSource
import com.isfandroid.pomodaily.data.source.local.PomodoroLocalDataSource
import com.isfandroid.pomodaily.data.source.local.TaskLocalDataSource
import com.isfandroid.pomodaily.data.source.repository.GeneralRepository
import com.isfandroid.pomodaily.data.source.repository.PomodoroRepository
import com.isfandroid.pomodaily.data.source.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideGeneralRepository(localDataSource: GeneralLocalDataSource) = GeneralRepository(localDataSource)

    @Provides
    @Singleton
    fun providePomodoroRepository(localDataSource: PomodoroLocalDataSource) = PomodoroRepository(localDataSource)

    @Provides
    @Singleton
    fun provideTaskRepository(localDataSource: TaskLocalDataSource) = TaskRepository(localDataSource)
}