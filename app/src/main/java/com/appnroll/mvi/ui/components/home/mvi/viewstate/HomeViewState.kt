package com.appnroll.mvi.ui.components.home.mvi.viewstate

import com.appnroll.mvi.common.mvi.viewstate.MviViewState
import com.appnroll.mvi.common.mvi.viewstate.ViewStateEmptyEvent
import com.appnroll.mvi.common.mvi.viewstate.ViewStateErrorEvent
import com.appnroll.mvi.ui.components.home.mvi.action.HomeAction
import com.appnroll.mvi.ui.components.home.mvi.action.HomeAction.AddTaskAction
import com.appnroll.mvi.ui.components.home.mvi.action.HomeAction.DeleteCompletedTasksAction
import com.appnroll.mvi.ui.components.home.mvi.action.HomeAction.LoadTasksAction
import com.appnroll.mvi.ui.components.home.mvi.action.HomeAction.UpdateTaskAction
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
    val error: ViewStateErrorEvent?,
    override val isSavable: Boolean = !inProgress
) : MviViewState {

    fun loadDataIfNeeded(): HomeAction? {
        return if (tasks == null) LoadTasksAction else null
    }

    fun addTask(taskContent: String): HomeAction? {
        return if (!taskContent.isBlank()) AddTaskAction(taskContent) else null
    }

    fun deleteCompletedTasks(): HomeAction? {
        return DeleteCompletedTasksAction
    }

    fun updateTask(taskId: Long, isDone: Boolean): HomeAction? {
        return UpdateTaskAction(taskId, isDone)
    }
}