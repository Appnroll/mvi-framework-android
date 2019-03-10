package com.appnroll.mvi.ui.components.home.mvi

import com.appnroll.mvi.data.repositories.TaskRepository
import com.appnroll.mvi.ui.base.mvi.*
import com.appnroll.mvi.ui.components.home.mvi.HomeAction.*
import com.appnroll.mvi.ui.components.home.mvi.HomeResult.*
import com.appnroll.mvi.ui.model.Task
import io.reactivex.Observable
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject


class HomeActionProcessor: MviActionsProcessor<HomeAction, HomeResult>(), KoinComponent {

    private val schedulerProvider: SchedulerProvider by inject()
    private val taskRepository: TaskRepository by inject()

    override fun getActionProcessors(shared: Observable<HomeAction>) = listOf(
        shared.connect(loadTasksActionProcessor),
        shared.connect(addTaskActionProcessor),
        shared.connect(updateTaskActionProcessor),
        shared.connect(deletedCompletedTasksActionProcessor)
    )

    private val loadTasksActionProcessor = createActionProcessor<LoadTasksAction, HomeResult>(
        schedulerProvider,
        { InProgress },
        { t -> Error(t) }
    ) {
        onNextSafe(LoadTasksResult(taskRepository.getAllTasks()))
        onCompleteSafe()
    }

    private val addTaskActionProcessor = createActionProcessor<AddTaskAction, HomeResult>(
        schedulerProvider,
        { InProgress },
        { t -> Error(t) }
    ) { action ->
        val newTask = taskRepository.addTask(Task(0, action.taskContent, false))
        onNextSafe(AddTaskResult(newTask))
        onCompleteSafe()
    }

    private val updateTaskActionProcessor = createActionProcessor<UpdateTaskAction, HomeResult>(
        schedulerProvider,
        { InProgress },
        { t -> Error(t) }
    ) { action ->
        val task = taskRepository.getTask(action.taskId)?.copy(isDone = action.isDone)
        val updateTaskResult = if (task == null) {
            Error(Exception("Task with id ${action.taskId} not found in DB"))
        } else {
            taskRepository.updateTask(task)
            UpdateTaskResult(task)
        }
        onNextSafe(updateTaskResult)
        onCompleteSafe()
    }

    private val deletedCompletedTasksActionProcessor = createActionProcessor<DeleteCompletedTasksAction, HomeResult>(
        schedulerProvider,
        { InProgress},
        { t -> Error(t) }
    ) {
        val doneTasks = taskRepository.getAllDoneTasks()
        taskRepository.delete(doneTasks)
        onNextSafe(DeleteCompletedTasksResult(doneTasks))
        onCompleteSafe()
    }
}