package com.isfandroid.pomodaily.di

import com.isfandroid.pomodaily.data.source.local.general.GeneralLocalDataSource
import com.isfandroid.pomodaily.data.source.local.pomodoro.PomodoroLocalDataSource
import com.isfandroid.pomodaily.data.source.local.task.TaskLocalDataSource
import com.isfandroid.pomodaily.data.source.repository.general.GeneralRepository
import com.isfandroid.pomodaily.data.source.repository.general.GeneralRepositoryImpl
import com.isfandroid.pomodaily.data.source.repository.pomodoro.PomodoroRepository
import com.isfandroid.pomodaily.data.source.repository.pomodoro.PomodoroRepositoryImpl
import com.isfandroid.pomodaily.data.source.repository.task.TaskRepository
import com.isfandroid.pomodaily.data.source.repository.task.TaskRepositoryImpl
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
    fun provideGeneralRepository(localDataSource: GeneralLocalDataSource): GeneralRepository = GeneralRepositoryImpl(localDataSource)

    @Provides
    @Singleton
    fun providePomodoroRepository(localDataSource: PomodoroLocalDataSource): PomodoroRepository = PomodoroRepositoryImpl(localDataSource)

    @Provides
    @Singleton
    fun provideTaskRepository(localDataSource: TaskLocalDataSource): TaskRepository = TaskRepositoryImpl(localDataSource)
}