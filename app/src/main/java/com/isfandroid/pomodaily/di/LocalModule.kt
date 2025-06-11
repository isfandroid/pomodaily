package com.isfandroid.pomodaily.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.isfandroid.pomodaily.AppDatabase
import com.isfandroid.pomodaily.data.source.local.GeneralLocalDataSource
import com.isfandroid.pomodaily.data.source.local.PomodoroLocalDataSource
import com.isfandroid.pomodaily.data.source.local.TaskLocalDataSource
import com.isfandroid.pomodaily.utils.Constant.APP_PREFS
import com.isfandroid.pomodaily.utils.Constant.DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun provideSqlDriver(@ApplicationContext appContext: Context): SqlDriver {
        return AndroidSqliteDriver(
            schema = AppDatabase.Schema,
            context = appContext,
            name = DB_NAME,
            callback = object : AndroidSqliteDriver.Callback(AppDatabase.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            }
        )
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext appContext: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            produceFile = { appContext.preferencesDataStoreFile(APP_PREFS) }
        )
    }

    @Provides
    @Singleton
    fun provideGeneralDataSource(dataStore: DataStore<Preferences>) = GeneralLocalDataSource(dataStore)

    @Provides
    @Singleton
    fun providePomodoroDataSource(dataStore: DataStore<Preferences>) = PomodoroLocalDataSource(dataStore)

    @Provides
    @Singleton
    fun provideTaskDataSource(
        dataStore: DataStore<Preferences>,
        driver: SqlDriver
    ) = TaskLocalDataSource(dataStore, AppDatabase(driver))
}