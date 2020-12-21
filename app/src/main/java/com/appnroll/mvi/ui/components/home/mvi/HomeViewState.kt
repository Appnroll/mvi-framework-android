package com.appnroll.mvi.ui.components.home.mvi

import com.appnroll.mvi.common.mvi.api.MviViewState
import com.appnroll.mvi.common.ViewStateEmptyEvent
import com.appnroll.mvi.common.ViewStateErrorEvent
import com.appnroll.mvi.model.Task
import kotlinx.android.parcel.Parcelize

/**
 * Describes the UI state of the Home feature - fragment is using it to render the views
 */
@Parcelize
data class HomeViewState(
    val inProgress: Boolean,
    val tasks: List<Task>?,
    val newTaskAdded: ViewStateEmptyEvent?,
    val error: ViewStateErrorEvent?
) : MviViewState {

    override fun isSavable(): Boolean = !inProgress

    fun inProgress() = copy(
        inProgress = true
    )

    fun tasksLoaded(tasks: List<Task>) = copy(
        inProgress = false,
        tasks = tasks
    )

    fun error(t: Throwable) = copy(
        inProgress = false,
        error = ViewStateErrorEvent(t)
    )

    fun taskAdded(task: Task) = copy(
        inProgress = false,
        tasks = tasks?.toMutableList()?.apply { add(task) }?.toList(),
        newTaskAdded = ViewStateEmptyEvent()
    )

    fun taskUpdated(task: Task) = copy(
        inProgress = false,
        tasks = tasks?.map {
            if (it.id == task.id) task else it
        } // replace updated task in the tasks list
    )

    fun tasksDeleted(tasks: List<Task>) = copy(
        inProgress = false,
        tasks = with(tasks.map { it.id }) {
            this@HomeViewState.tasks?.filterNot { it.id in this }
        }
    )
}