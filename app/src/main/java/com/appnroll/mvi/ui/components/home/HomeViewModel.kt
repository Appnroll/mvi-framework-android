package com.appnroll.mvi.ui.components.home

import com.appnroll.mvi.ui.base.mvi.MviViewModel
import com.appnroll.mvi.ui.components.home.mvi.HomeAction
import com.appnroll.mvi.ui.components.home.mvi.HomeActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.HomeResult


class HomeViewModel: MviViewModel<HomeAction, HomeResult, HomeViewState>(
    HomeActionProcessor(),
    HomeViewState.default()
) {

    override fun initialAction(): HomeAction? {
        return if (viewState.tasks == null) {
            HomeAction.LoadTasksAction
        } else {
            null
        }
    }

    fun addTask(taskContent: String) {
        if (!taskContent.isBlank()) {
            accept(HomeAction.AddTaskAction(taskContent))
        }
    }

    fun deleteCompletedTasks() {
        accept(HomeAction.DeleteCompletedTasksAction)
    }

    fun updateTask(taskId: Long, isDone: Boolean) {
        accept(HomeAction.UpdateTaskAction(taskId, isDone))
    }
}