package com.appnroll.mvi.ui.components.home.mvi.state

import com.appnroll.mvi.common.mvi.state.MviViewState
import com.appnroll.mvi.ui.base.mvi.ViewStateEmptyEvent
import com.appnroll.mvi.ui.base.mvi.ViewStateErrorEvent
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction.AddTaskAction
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction.DeleteCompletedTasksAction
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction.LoadTasksAction
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction.UpdateTaskAction
import com.appnroll.mvi.ui.model.Task
import kotlinx.android.parcel.Parcelize

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