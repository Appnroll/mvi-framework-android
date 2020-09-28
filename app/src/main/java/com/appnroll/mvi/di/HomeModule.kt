package com.appnroll.mvi.di

import androidx.lifecycle.SavedStateHandle
import com.appnroll.mvi.ui.components.home.mvi.HomeActionProcessing
import com.appnroll.mvi.ui.components.home.mvi.HomeMviController
import com.appnroll.mvi.ui.components.home.mvi.HomeResultProcessing
import com.appnroll.mvi.ui.components.home.HomeViewModel
import com.appnroll.mvi.ui.components.home.mvi.HomeViewStateCache
import com.appnroll.mvi.ui.components.home.mvi.impl.AddTaskActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.impl.DeleteCompletedTasksActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.impl.HomeActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.impl.LoadTasksActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.impl.UpdateTaskActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.impl.HomeResultReducer
import kotlinx.coroutines.CoroutineScope
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf

inline val Module.HomeModule
    get() = configure {
        factory {
            HomeActionProcessor(
                loadTasksActionProcessor = LoadTasksActionProcessor(get()),
                addTaskActionProcessor = AddTaskActionProcessor(get()),
                updateTaskActionProcessor = UpdateTaskActionProcessor(get(), get()),
                deleteCompletedTasksActionProcessor = DeleteCompletedTasksActionProcessor(get(), get())
            )
        }

        factory {
            HomeActionProcessing(
                homeActionProcessor = get()
            )
        }

        factory { HomeResultReducer() }

        factory { (savedStateHandle: SavedStateHandle) ->
            HomeViewStateCache(
                savedStateHandle = savedStateHandle
            )
        }

        factory { (savedStateHandle: SavedStateHandle) ->
            HomeResultProcessing(
                homeResultReducer = get(),
                homeViewStateCache = get { parametersOf(savedStateHandle) }
            )
        }

        factory { (savedStateHandle: SavedStateHandle, coroutineScope: CoroutineScope) ->
            HomeMviController(
                homeActionProcessing = get(),
                homeResultProcessing = get { parametersOf(savedStateHandle) },
                homeViewStateCache = get { parametersOf(savedStateHandle) },
                coroutineScope = coroutineScope
            )
        }

        viewModel { (savedStateHandle: SavedStateHandle) ->
            HomeViewModel(savedStateHandle = savedStateHandle)
        }
    }