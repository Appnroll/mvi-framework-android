package com.appnroll.mvi.di

import androidx.lifecycle.SavedStateHandle
import com.appnroll.mvi.common.mvi.MviController
import com.appnroll.mvi.common.mvi.MviViewStateCache
import com.appnroll.mvi.common.mvi.MviViewStateCacheImpl
import com.appnroll.mvi.common.mvi.state.MviStateProcessingFlow
import com.appnroll.mvi.ui.components.home.mvi.model.HomeActionProcessingFlow
import com.appnroll.mvi.ui.components.home.mvi.HomeViewModel
import com.appnroll.mvi.ui.components.home.mvi.model.AddTaskActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.model.DeleteCompletedTasksActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult
import com.appnroll.mvi.ui.components.home.mvi.model.LoadTasksActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.model.UpdateTaskActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.state.HomeStateReducer
import com.appnroll.mvi.ui.components.home.mvi.state.HomeViewState
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf

inline val Module.HomeModule
    get() = configure {
        factory {
            HomeActionProcessingFlow(
                loadTasksActionProcessor = LoadTasksActionProcessor(get()),
                addTaskActionProcessor = AddTaskActionProcessor(get()),
                updateTaskActionProcessor = UpdateTaskActionProcessor(get(), get()),
                deleteCompletedTasksActionProcessor = DeleteCompletedTasksActionProcessor(get(), get())
            )
        }

        factory { HomeStateReducer() }

        factory { (mviViewStateCache: MviViewStateCache<HomeViewState>) ->
            MviStateProcessingFlow<HomeResult, HomeViewState>(
                mviViewStateCache = mviViewStateCache,
                mviStateReducer = get<HomeStateReducer>()
            )
        }

        factory<MviViewStateCache<HomeViewState>> { (saveStateHandle: SavedStateHandle) ->
            MviViewStateCacheImpl("HomeViewStateKey", saveStateHandle)
        }

        // Mvi controller is extracted from view model
        factory<MviController<HomeAction, HomeResult, HomeViewState>> { (saveStateHandle: SavedStateHandle) ->
            val mviViewStateCache = get<MviViewStateCache<HomeViewState>> { parametersOf(saveStateHandle) }
            MviController(
                mviActionProcessingFlow = get<HomeActionProcessingFlow>(),
                mviStateProcessingFlow = get { parametersOf(mviViewStateCache) },
                mviViewStateCache = mviViewStateCache
            )
        }

        viewModel { (handle: SavedStateHandle) ->
            HomeViewModel(
                homeMviController = get { parametersOf(handle) }
            )
        }
    }