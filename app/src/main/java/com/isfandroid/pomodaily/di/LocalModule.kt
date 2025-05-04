package com.isfandroid.pomodaily.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import com.isfandroid.pomodaily.data.source.local.database.AppDao
import com.isfandroid.pomodaily.data.source.local.database.AppDatabase
import com.isfandroid.pomodaily.utils.Constant.APP_PREFS
import com.isfandroid.pomodaily.utils.Constant.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LocalModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { appContext.preferencesDataStoreFile(APP_PREFS) }
        )
    }

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        DB_NAME
    ).fallbackToDestructiveMigration(false).build()

    @Provides
    @Singleton
    fun provideAppDao(database: AppDatabase): AppDao = database.appDao()
}