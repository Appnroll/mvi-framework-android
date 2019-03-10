package com.appnroll.mvi.ui.components.home.mvi

import com.appnroll.mvi.ui.base.mvi.MviAction


sealed class HomeAction: MviAction {

    object LoadTasksAction: HomeAction()

    data class AddTaskAction(val taskContent: String): HomeAction()

    data class UpdateTaskAction(val taskId: Long, val isDone: Boolean): HomeAction()

    object DeleteCompletedTasksAction: HomeAction()
}