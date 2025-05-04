package com.isfandroid.pomodaily.data.source.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.isfandroid.pomodaily.data.source.local.model.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun appDao(): AppDao
}