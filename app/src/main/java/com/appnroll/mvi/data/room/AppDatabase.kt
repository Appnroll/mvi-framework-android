package com.appnroll.mvi.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.appnroll.mvi.data.room.dao.TaskDao
import com.appnroll.mvi.data.room.entities.TaskEntity

@Database(
    entities = [
        TaskEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun taskDao(): TaskDao
}