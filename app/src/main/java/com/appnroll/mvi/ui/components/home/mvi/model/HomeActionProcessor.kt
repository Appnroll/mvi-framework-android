package com.appnroll.mvi.ui.components.home.mvi.model

import com.appnroll.mvi.common.mvi.model.mviProcessor
import com.appnroll.mvi.data.usecases.AddTaskUseCase
import com.appnroll.mvi.data.usecases.DeleteTasksUseCase
import com.appnroll.mvi.data.usecases.GetAllDoneTasksUseCase
import com.appnroll.mvi.data.usecases.GetAllTasksUseCase
import com.appnroll.mvi.data.usecases.GetTaskUseCase
import com.appnroll.mvi.data.usecases.UpdateTaskUseCase
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

val loadTasksActionProcessor = mviProcessor { _: LoadTasksAction ->
    val getAllTasksUseCase: GetAllTasksUseCase = KoinContextHandler.get().get()

    flow {
        emit(InProgressResult)
        emit(LoadTasksResult(getAllTasksUseCase(Unit)))
    }.catch {
        emit(ErrorResult(it))
    }
}

val addTaskActionProcessor = mviProcessor { action: AddTaskAction ->
    val addTaskUseCase: AddTaskUseCase = KoinContextHandler.get().get()

    flow {
        emit(InProgressResult)

        val newTask = addTaskUseCase(Task(0, action.taskContent, false))
        emit(AddTaskResult(newTask))
    }.catch {
        emit(ErrorResult(it))
    }
}

val updateTaskActionProcessor = mviProcessor { action: UpdateTaskAction ->
    val getTaskUseCase: GetTaskUseCase = KoinContextHandler.get().get()
    val updateTaskUseCase: UpdateTaskUseCase = KoinContextHandler.get().get()

    flow {
        emit(InProgressResult)

        val task = getTaskUseCase(action.taskId)?.copy(isDone = action.isDone)
        val updateTaskResult = if (task == null) {
            ErrorResult(Exception("Task with id ${action.taskId} not found in DB"))
        } else {
            updateTaskUseCase(task)
            UpdateTaskResult(task)
        }
        emit(updateTaskResult)
    }.catch {
        emit(ErrorResult(it))
    }
}

val deletedCompletedTasksActionProcessor = mviProcessor { _: DeleteCompletedTasksAction ->
    val getAllDoneTasksUseCase: GetAllDoneTasksUseCase = KoinContextHandler.get().get()
    val deleteTasksUseCase: DeleteTasksUseCase = KoinContextHandler.get().get()
    flow {
        emit(InProgressResult)

        val doneTasks = getAllDoneTasksUseCase(Unit)
        deleteTasksUseCase(doneTasks)
        emit(DeleteCompletedTasksResult(doneTasks))
    }.catch {
        emit(ErrorResult(it))
    }
}
