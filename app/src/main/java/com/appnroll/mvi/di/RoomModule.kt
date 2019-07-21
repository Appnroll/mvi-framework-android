package com.appnroll.mvi.di

import androidx.room.Room
import com.appnroll.mvi.data.room.AppDatabase
import org.koin.dsl.module


val roomModule = module {

    single {
        Room.databaseBuilder(get(), AppDatabase::class.java, "app-database")
            .fallbackToDestructiveMigration()
            .build()
    }

    factory { get<AppDatabase>().taskDao() }
}