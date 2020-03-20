package com.appnroll.mvi.ui.components.home.mvi

import com.appnroll.mvi.data.repositories.TaskRepository
import com.appnroll.mvi.ui.base.mvi.MviActionsProcessor
import com.appnroll.mvi.ui.base.mvi.SchedulersProvider
import com.appnroll.mvi.ui.base.mvi.createActionProcessor
import com.appnroll.mvi.ui.base.mvi.onCompleteSafe
import com.appnroll.mvi.ui.base.mvi.onNextSafe
import com.appnroll.mvi.ui.components.home.mvi.HomeAction.AddTaskAction
import com.appnroll.mvi.ui.components.home.mvi.HomeAction.DeleteCompletedTasksAction
import com.appnroll.mvi.ui.components.home.mvi.HomeAction.LoadTasksAction
import com.appnroll.mvi.ui.components.home.mvi.HomeAction.UpdateTaskAction
import com.appnroll.mvi.ui.components.home.mvi.HomeResult.AddTaskResult
import com.appnroll.mvi.ui.components.home.mvi.HomeResult.DeleteCompletedTasksResult
import com.appnroll.mvi.ui.components.home.mvi.HomeResult.ErrorResult
import com.appnroll.mvi.ui.components.home.mvi.HomeResult.InProgressResult
import com.appnroll.mvi.ui.components.home.mvi.HomeResult.LoadTasksResult
import com.appnroll.mvi.ui.components.home.mvi.HomeResult.UpdateTaskResult
import com.appnroll.mvi.ui.model.Task
import io.reactivex.Observable
import org.koin.core.KoinComponent
import org.koin.core.inject

class HomeActionProcessor: MviActionsProcessor<HomeAction, HomeResult>(), KoinComponent {

    private val schedulersProvider: SchedulersProvider by inject()
    private val taskRepository: TaskRepository by inject()

    override fun getActionProcessors(shared: Observable<HomeAction>) = listOf(
        shared.connect(loadTasksActionProcessor),
        shared.connect(addTaskActionProcessor),
        shared.connect(updateTaskActionProcessor),
        shared.connect(deletedCompletedTasksActionProcessor)
    )

    private val loadTasksActionProcessor = createActionProcessor<LoadTasksAction, HomeResult>(
        schedulersProvider,
        { InProgressResult },
        { t -> ErrorResult(t) }
    ) {
        onNextSafe(LoadTasksResult(taskRepository.getAllTasks()))
        onCompleteSafe()
    }

    private val addTaskActionProcessor = createActionProcessor<AddTaskAction, HomeResult>(
        schedulersProvider,
        { InProgressResult },
        { t -> ErrorResult(t) }
    ) { action ->
        val newTask = taskRepository.addTask(Task(0, action.taskContent, false))
        onNextSafe(AddTaskResult(newTask))
        onCompleteSafe()
    }

    private val updateTaskActionProcessor = createActionProcessor<UpdateTaskAction, HomeResult>(
        schedulersProvider,
        { InProgressResult },
        { t -> ErrorResult(t) }
    ) { action ->
        val task = taskRepository.getTask(action.taskId)?.copy(isDone = action.isDone)
        val updateTaskResult = if (task == null) {
            ErrorResult(Exception("Task with id ${action.taskId} not found in DB"))
        } else {
            taskRepository.updateTask(task)
            UpdateTaskResult(task)
        }
        onNextSafe(updateTaskResult)
        onCompleteSafe()
    }

    private val deletedCompletedTasksActionProcessor = createActionProcessor<DeleteCompletedTasksAction, HomeResult>(
        schedulersProvider,
        { InProgressResult},
        { t -> ErrorResult(t) }
    ) {
        val doneTasks = taskRepository.getAllDoneTasks()
        taskRepository.delete(doneTasks)
        onNextSafe(DeleteCompletedTasksResult(doneTasks))
        onCompleteSafe()
    }
}