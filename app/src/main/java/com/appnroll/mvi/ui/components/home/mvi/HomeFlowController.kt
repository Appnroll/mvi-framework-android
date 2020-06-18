package com.appnroll.mvi.ui.components.home.mvi

import androidx.lifecycle.SavedStateHandle
import com.appnroll.mvi.common.mvi.MviFlowController
import com.appnroll.mvi.common.mvi.model.modelControllerOf
import com.appnroll.mvi.common.mvi.ui.stateControllerOf
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction.AddTaskAction
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction.DeleteCompletedTasksAction
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction.LoadTasksAction
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction.UpdateTaskAction
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult
import com.appnroll.mvi.ui.components.home.mvi.model.addTaskActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.model.deletedCompletedTasksActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.model.loadTasksActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.model.updateTaskActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.state.HomeReducer
import com.appnroll.mvi.ui.components.home.mvi.state.HomeViewState

class HomeFlowController(
    savedStateHandle: SavedStateHandle
) : MviFlowController<HomeAction, HomeResult, HomeViewState>(
    savedStateHandle = savedStateHandle,
    modelController = modelControllerOf { action: HomeAction ->
        when (action) {
            is LoadTasksAction -> loadTasksActionProcessor(action)
            is AddTaskAction -> addTaskActionProcessor(action)
            is UpdateTaskAction -> updateTaskActionProcessor(action)
            is DeleteCompletedTasksAction -> deletedCompletedTasksActionProcessor(action)
        }
    },
    stateControllerBuilder = { savedState ->
        stateControllerOf(initial = savedState, reducer = HomeReducer())
    }
)
