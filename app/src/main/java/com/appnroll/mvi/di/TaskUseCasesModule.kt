package com.appnroll.mvi.di

import com.appnroll.mvi.data.usecases.AddTaskUseCaseImpl
import com.appnroll.mvi.data.usecases.AddTaskUseCase
import com.appnroll.mvi.data.usecases.DeleteTasksUseCase
import com.appnroll.mvi.data.usecases.DeleteTasksUseCaseImpl
import com.appnroll.mvi.data.usecases.GetAllDoneTasksUseCase
import com.appnroll.mvi.data.usecases.GetAllDoneTasksUseCaseImpl
import com.appnroll.mvi.data.usecases.GetAllTasksUseCase
import com.appnroll.mvi.data.usecases.GetAllTasksUseCaseImpl
import com.appnroll.mvi.data.usecases.GetTaskUseCase
import com.appnroll.mvi.data.usecases.GetTaskUseCaseImpl
import com.appnroll.mvi.data.usecases.UpdateTaskUseCase
import com.appnroll.mvi.data.usecases.UpdateTaskUseCaseImpl
import org.koin.core.module.Module

inline val Module.RepositoryModule
    get() = configure {
        factory<AddTaskUseCase> { AddTaskUseCaseImpl(get()) }
        factory<GetTaskUseCase> { GetTaskUseCaseImpl(get()) }
        factory<GetAllTasksUseCase> { GetAllTasksUseCaseImpl(get()) }
        factory<GetAllDoneTasksUseCase> { GetAllDoneTasksUseCaseImpl(get()) }
        factory<UpdateTaskUseCase> { UpdateTaskUseCaseImpl(get()) }
        factory<DeleteTasksUseCase> { DeleteTasksUseCaseImpl(get()) }
    }

