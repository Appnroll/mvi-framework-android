package com.appnroll.mvi.ui.components.home.mvi.model

import com.appnroll.mvi.common.mvi.model.mviProcessor
import com.appnroll.mvi.data.repositories.TaskRepository
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction.AddTaskAction
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction.DeleteCompletedTasksAction
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction.LoadTasksAction
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction.UpdateTaskAction
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult.AddTaskResult
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult.DeleteCompletedTasksResult
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult.ErrorResult
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult.InProgressResult
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult.LoadTasksResult
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult.UpdateTaskResult
import com.appnroll.mvi.ui.model.Task
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import org.koin.core.context.KoinContextHandler

val loadTasksActionProcessor = mviProcessor { action: LoadTasksAction ->
    val taskRepository = KoinContextHandler.get().get<TaskRepository>()

    flow {
        emit(InProgressResult)
        emit(LoadTasksResult(taskRepository.getAllTasks()))
    }.catch {
        emit(ErrorResult(it))
    }
}

val addTaskActionProcessor = mviProcessor { action: AddTaskAction ->
    val taskRepository = KoinContextHandler.get().get<TaskRepository>()

    flow {
        emit(InProgressResult)

        val newTask = taskRepository.addTask(Task(0, action.taskContent, false))
        emit(AddTaskResult(newTask))
    }.catch {
        emit(ErrorResult(it))
    }
}

val updateTaskActionProcessor = mviProcessor { action: UpdateTaskAction ->
    val taskRepository = KoinContextHandler.get().get<TaskRepository>()

    flow {
        emit(InProgressResult)

        val task = taskRepository.getTask(action.taskId)?.copy(isDone = action.isDone)
        val updateTaskResult = if (task == null) {
            ErrorResult(Exception("Task with id ${action.taskId} not found in DB"))
        } else {
            taskRepository.updateTask(task)
            UpdateTaskResult(task)
        }
        emit(updateTaskResult)
    }.catch {
        emit(ErrorResult(it))
    }
}

val deletedCompletedTasksActionProcessor = mviProcessor { action: DeleteCompletedTasksAction ->
    val taskRepository = KoinContextHandler.get().get<TaskRepository>()
    flow {
        emit(InProgressResult)

        val doneTasks = taskRepository.getAllDoneTasks()
        taskRepository.delete(doneTasks)
        emit(DeleteCompletedTasksResult(doneTasks))
    }.catch {
        emit(ErrorResult(it))
    }
}
