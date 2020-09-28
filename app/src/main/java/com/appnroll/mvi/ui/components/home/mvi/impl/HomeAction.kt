package com.appnroll.mvi.ui.components.home.mvi.impl

import com.appnroll.mvi.common.mvi.api.MviAction

/**
 * Set of action which could be done in Home feature
 */
sealed class HomeAction : MviAction {

    object LoadTasksAction : HomeAction()

    data class AddTaskAction(val taskContent: String) : HomeAction()

    data class UpdateTaskAction(val taskId: Long, val isDone: Boolean) : HomeAction() {
        override fun getId() = super.getId().toString() + taskId
    }

    object DeleteCompletedTasksAction : HomeAction()
}