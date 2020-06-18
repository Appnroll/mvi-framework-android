package com.appnroll.mvi.ui.components.home.mvi.state

import com.appnroll.mvi.common.mvi.ui.MviReducer
import com.appnroll.mvi.ui.base.mvi.ViewStateEmptyEvent
import com.appnroll.mvi.ui.base.mvi.ViewStateErrorEvent
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult.AddTaskResult
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult.DeleteCompletedTasksResult
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult.ErrorResult
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult.InProgressResult
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult.LoadTasksResult
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult.UpdateTaskResult

class HomeReducer :
    MviReducer<HomeResult, HomeViewState> {
    override fun HomeViewState.reduce(result: HomeResult): HomeViewState {
        return when (result) {
            is InProgressResult -> reduce(result)
            is ErrorResult -> reduce(result)
            is LoadTasksResult -> reduce(result)
            is AddTaskResult -> reduce(result)
            is UpdateTaskResult -> reduce(result)
            is DeleteCompletedTasksResult -> reduce(result)
        }
    }

    override fun default(): HomeViewState =
        HomeViewState(
            false,
            null,
            null,
            null
        )

    private fun HomeViewState.reduce(@Suppress("UNUSED_PARAMETER") result: InProgressResult) =
        copy(inProgress = true)

    private fun HomeViewState.reduce(result: ErrorResult) =
        copy(inProgress = false, error = ViewStateErrorEvent(result.t))

    private fun HomeViewState.reduce(result: LoadTasksResult) =
        copy(inProgress = false, tasks = result.tasks)

    private fun HomeViewState.reduce(result: AddTaskResult) =
        copy(
            inProgress = false,
            tasks = tasks?.toMutableList()?.apply { add(result.addedTask) }?.toList(),
            newTaskAdded = ViewStateEmptyEvent()
        )

    private fun HomeViewState.reduce(result: UpdateTaskResult) =
        copy(
            inProgress = false,
            tasks = tasks?.map {
                if (it.id == result.updatedTask.id) result.updatedTask else it
            } // replace updated task in the tasks list
        )

    private fun HomeViewState.reduce(result: DeleteCompletedTasksResult) =
        copy(
            inProgress = false,
            tasks = with(result.deletedTasks.map { it.id }) {
                tasks?.filterNot { it.id in this }
            }
        )
}
