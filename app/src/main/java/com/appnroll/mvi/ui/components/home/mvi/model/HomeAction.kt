package com.appnroll.mvi.ui.components.home.mvi.model

import com.appnroll.mvi.common.mvi.model.MviAction

sealed class HomeAction : MviAction {

    object LoadTasksAction : HomeAction()

    data class AddTaskAction(val taskContent: String) : HomeAction()

    data class UpdateTaskAction(val taskId: Long, val isDone: Boolean) : HomeAction()

    object DeleteCompletedTasksAction : HomeAction()
}