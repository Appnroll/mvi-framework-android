package com.appnroll.mvi.ui.components.home

import com.appnroll.mvi.ui.base.mvi.MviReducer
import com.appnroll.mvi.ui.base.mvi.ViewStateEmptyEvent
import com.appnroll.mvi.ui.base.mvi.ViewStateErrorEvent
import com.appnroll.mvi.ui.components.home.mvi.HomeResult
import com.appnroll.mvi.ui.components.home.mvi.HomeResult.AddTaskResult
import com.appnroll.mvi.ui.components.home.mvi.HomeResult.DeleteCompletedTasksResult
import com.appnroll.mvi.ui.components.home.mvi.HomeResult.ErrorResult
import com.appnroll.mvi.ui.components.home.mvi.HomeResult.InProgressResult
import com.appnroll.mvi.ui.components.home.mvi.HomeResult.LoadTasksResult
import com.appnroll.mvi.ui.components.home.mvi.HomeResult.UpdateTaskResult

class HomeReducer :
    MviReducer<HomeResult, HomeViewState> {
    override fun reduce(current: HomeViewState, result: HomeResult): HomeViewState {
        return when (result) {
            is InProgressResult -> reduce(current, result)
            is ErrorResult -> reduce(current, result)
            is LoadTasksResult -> reduce(current, result)
            is AddTaskResult -> reduce(current, result)
            is UpdateTaskResult -> reduce(current, result)
            is DeleteCompletedTasksResult -> reduce(current, result)
        }
    }

    override fun default(): HomeViewState =
        HomeViewState(false, null, null, null)

    private fun reduce(state: HomeViewState, result: InProgressResult) =
        state.copy(
            inProgress = true
        )

    private fun reduce(state: HomeViewState, result: ErrorResult) =
        state.copy(
            inProgress = false,
            error = ViewStateErrorEvent(result.t)
        )

    private fun reduce(state: HomeViewState, result: LoadTasksResult) =
        state.copy(
            inProgress = false,
            tasks = result.tasks
        )

    private fun reduce(state: HomeViewState, result: AddTaskResult) =
        state.copy(
            inProgress = false,
            tasks = state.tasks?.toMutableList()?.apply { add(result.addedTask) }?.toList(),
            newTaskAdded = ViewStateEmptyEvent()
        )

    private fun reduce(state: HomeViewState, result: UpdateTaskResult) =
        state.copy(
            inProgress = false,
            tasks = state.tasks?.map {
                if (it.id == result.updatedTask.id) result.updatedTask else it
            } // replace updated task in the tasks list
        )

    private fun reduce(state: HomeViewState, result: DeleteCompletedTasksResult): HomeViewState {
        val deletedIds = result.deletedTasks.map { it.id }
        val newTasks = state.tasks?.toMutableList()
        newTasks?.removeAll { deletedIds.contains(it.id) }
        return state.copy(
            inProgress = false,
            tasks = newTasks
        )
    }
}