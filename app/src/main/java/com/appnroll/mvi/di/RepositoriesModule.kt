package com.appnroll.mvi.di

import com.appnroll.mvi.data.repositories.TaskRepository
import com.appnroll.mvi.data.repositories.TaskRepositoryImpl
import org.koin.core.module.Module

inline val Module.RepositoryModule
    get() = configure {
        factory<TaskRepository> { TaskRepositoryImpl(get()) }
    }
