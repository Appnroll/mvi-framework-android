package com.appnroll.mvi.di

import androidx.room.Room
import com.appnroll.mvi.data.room.AppDatabase
import org.koin.core.module.Module

inline val Module.RoomModule
    get() = configure {
        single {
            Room.databaseBuilder(get(), AppDatabase::class.java, "app-database")
                .fallbackToDestructiveMigration()
                .build()
        }

        factory { get<AppDatabase>().taskDao() }
    }