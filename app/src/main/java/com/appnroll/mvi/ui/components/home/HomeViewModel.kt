package com.appnroll.mvi.ui.components.home

import androidx.lifecycle.SavedStateHandle
import com.appnroll.mvi.ui.base.mvi.MviViewModel
import com.appnroll.mvi.ui.components.home.mvi.HomeAction
import com.appnroll.mvi.ui.components.home.mvi.HomeAction.AddTaskAction
import com.appnroll.mvi.ui.components.home.mvi.HomeAction.DeleteCompletedTasksAction
import com.appnroll.mvi.ui.components.home.mvi.HomeAction.LoadTasksAction
import com.appnroll.mvi.ui.components.home.mvi.HomeAction.UpdateTaskAction
import com.appnroll.mvi.ui.components.home.mvi.HomeActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.HomeResult


class HomeViewModel(savedStateHandle : SavedStateHandle): MviViewModel<HomeAction, HomeResult, HomeViewState>(
    savedStateHandle,
    HomeActionProcessor(),
    HomeViewState.default()
) {
    
    fun loadDataIfNeeded() {
        if (viewState.tasks == null) {
            accept(LoadTasksAction)
        }
    }
    
    fun addTask(taskContent: String) {
        if (!taskContent.isBlank()) {
            accept(AddTaskAction(taskContent))
        }
    }

    fun deleteCompletedTasks() {
        accept(DeleteCompletedTasksAction)
    }

    fun updateTask(taskId: Long, isDone: Boolean) {
        accept(UpdateTaskAction(taskId, isDone))
    }
}