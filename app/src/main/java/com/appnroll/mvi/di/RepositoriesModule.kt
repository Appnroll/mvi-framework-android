package com.appnroll.mvi.di

import com.appnroll.mvi.data.repositories.TaskRepository
import com.appnroll.mvi.data.repositories.TaskRepositoryImpl
import org.koin.dsl.module.module


val repositoriesModule = module {

    factory<TaskRepository> { TaskRepositoryImpl(get()) }
}