package com.appnroll.mvi.ui.components.home

import com.appnroll.mvi.ui.base.mvi.MviViewState
import com.appnroll.mvi.ui.base.mvi.ViewStateEmptyEvent
import com.appnroll.mvi.ui.base.mvi.ViewStateEvent
import com.appnroll.mvi.ui.components.home.mvi.HomeResult
import com.appnroll.mvi.ui.components.home.mvi.HomeResult.*
import com.appnroll.mvi.ui.model.Task
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HomeViewState(
    val inProgress: Boolean,
    val tasks: List<Task>?,
    val newTaskAdded: ViewStateEmptyEvent?,
    val error: ViewStateEvent<Throwable>?
): MviViewState<HomeResult> {

    companion object {

        fun default() = HomeViewState(false, null, null, null)
    }

    override fun reduce(result: HomeResult): HomeViewState {
        return when (result) {
            is InProgressResult -> result.reduce()
            is ErrorResult -> result.reduce()
            is LoadTasksResult -> result.reduce()
            is AddTaskResult -> result.reduce()
            is UpdateTaskResult -> result.reduce()
            is DeleteCompletedTasksResult -> result.reduce()
        }
    }

    private fun InProgressResult.reduce() = this@HomeViewState.copy(
        inProgress = true
    )

    private fun ErrorResult.reduce() = this@HomeViewState.copy(
        inProgress = false,
        error = ViewStateEvent(t)
    )

    private fun LoadTasksResult.reduce() = this@HomeViewState.copy(
        inProgress = false,
        tasks = tasks
    )

    private fun AddTaskResult.reduce() = this@HomeViewState.copy(
        inProgress = false,
        tasks = tasks?.toMutableList()?.apply { add(addedTask) }?.toList(),
        newTaskAdded = ViewStateEmptyEvent()
    )

    private fun UpdateTaskResult.reduce() = this@HomeViewState.copy(
        inProgress = false,
        tasks = tasks?.map { if (it.id == updatedTask.id) updatedTask else it } // replace updated task in the tasks list
    )

    private fun DeleteCompletedTasksResult.reduce(): HomeViewState {
        val deletedIds = deletedTasks.map { it.id }
        val newTasks = tasks?.toMutableList()
        newTasks?.removeAll { deletedIds.contains(it.id) }
        return this@HomeViewState.copy(
            inProgress = false,
            tasks = newTasks
        )
    }

    override fun isSavable() = !inProgress
}