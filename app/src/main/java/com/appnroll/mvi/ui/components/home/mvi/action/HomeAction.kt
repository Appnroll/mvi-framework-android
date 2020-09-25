package com.appnroll.mvi.ui.components.home.mvi.action

import com.appnroll.mvi.common.mvi.action.MviAction

/**
 * Set of action which could be done in Home feature
 */
sealed class HomeAction : MviAction {

    object LoadTasksAction : HomeAction()

    data class AddTaskAction(val taskContent: String) : HomeAction()

    data class UpdateTaskAction(val taskId: Long, val isDone: Boolean) : HomeAction()

    object DeleteCompletedTasksAction : HomeAction()
}