package com.appnroll.mvi.ui.components.home.mvi.result

import com.appnroll.mvi.common.mvi.result.MviResult
import com.appnroll.mvi.model.Task

/**
 * Set of results which could be returned after processing Home feature Action
 */
sealed class HomeResult : MviResult {

    object InProgressResult : HomeResult()

    data class ErrorResult(val t: Throwable) : HomeResult()

    data class LoadTasksResult(val tasks: List<Task>) : HomeResult()

    data class AddTaskResult(val addedTask: Task) : HomeResult()

    data class UpdateTaskResult(val updatedTask: Task) : HomeResult()

    data class DeleteCompletedTasksResult(val deletedTasks: List<Task>) : HomeResult()
}