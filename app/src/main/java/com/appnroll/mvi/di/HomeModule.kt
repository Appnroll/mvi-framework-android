package com.appnroll.mvi.di

import androidx.lifecycle.SavedStateHandle
import com.appnroll.mvi.ui.components.home.mvi.HomeActionProcessingFlow
import com.appnroll.mvi.ui.components.home.mvi.HomeMviController
import com.appnroll.mvi.ui.components.home.mvi.HomeResultProcessingFlow
import com.appnroll.mvi.ui.components.home.HomeViewModel
import com.appnroll.mvi.ui.components.home.mvi.HomeViewStateCache
import com.appnroll.mvi.ui.components.home.mvi.action.AddTaskActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.action.DeleteCompletedTasksActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.action.HomeActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.action.LoadTasksActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.action.UpdateTaskActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.result.HomeResultReducer
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
            HomeActionProcessingFlow(
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
            HomeResultProcessingFlow(
                homeResultReducer = get(),
                homeViewStateCache = get { parametersOf(savedStateHandle) }
            )
        }

        factory { (savedStateHandle: SavedStateHandle, coroutineScope: CoroutineScope) ->
            HomeMviController(
                homeActionProcessingFlow = get(),
                homeResultProcessingFlow = get { parametersOf(savedStateHandle) },
                homeViewStateCache = get { parametersOf(savedStateHandle) },
                coroutineScope = coroutineScope
            )
        }

        viewModel { (savedStateHandle: SavedStateHandle) ->
            HomeViewModel(savedStateHandle = savedStateHandle)
        }
    }