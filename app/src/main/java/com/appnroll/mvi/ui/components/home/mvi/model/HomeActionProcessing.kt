package com.appnroll.mvi.ui.components.home.mvi.model

import com.appnroll.mvi.common.mvi.model.MviActionProcessingFlow
import com.appnroll.mvi.common.mvi.model.MviProcessor
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

class HomeActionProcessingFlow(
    private val loadTasksActionProcessor: LoadTasksActionProcessor,
    private val addTaskActionProcessor: AddTaskActionProcessor,
    private val updateTaskActionProcessor: UpdateTaskActionProcessor,
    private val deleteCompletedTasksActionProcessor: DeleteCompletedTasksActionProcessor
) : MviActionProcessingFlow<HomeAction, HomeResult>() {

    override fun getProcessor(action: HomeAction) =
        when (action) {
            is LoadTasksAction -> loadTasksActionProcessor(action)
            is AddTaskAction -> addTaskActionProcessor(action)
            is UpdateTaskAction -> updateTaskActionProcessor(action)
            is DeleteCompletedTasksAction -> deleteCompletedTasksActionProcessor(action)
        }
}

/**
 * Processors:
 */
class LoadTasksActionProcessor(
    private val getAllTasksUseCase: GetAllTasksUseCase
): MviProcessor<LoadTasksAction, HomeResult> {

    override fun invoke(action: LoadTasksAction) = flow {
        emit(InProgressResult)
        emit(LoadTasksResult(getAllTasksUseCase()))
    }.catch {
        emit(ErrorResult(it))
    }
}

class AddTaskActionProcessor(
    private val addTaskUseCase: AddTaskUseCase
) : MviProcessor<AddTaskAction, HomeResult> {

    override fun invoke(action: AddTaskAction) = flow {
        emit(InProgressResult)

        val newTask = addTaskUseCase(Task(0, action.taskContent, false))
        emit(AddTaskResult(newTask))
    }.catch {
        emit(ErrorResult(it))
    }
}

class UpdateTaskActionProcessor(
    private val getTaskUseCase: GetTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
): MviProcessor<UpdateTaskAction, HomeResult> {

    override fun invoke(action: UpdateTaskAction) = flow {
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

class DeleteCompletedTasksActionProcessor(
    private val getAllDoneTasksUseCase: GetAllDoneTasksUseCase,
    private val deleteTasksUseCase: DeleteTasksUseCase
): MviProcessor<DeleteCompletedTasksAction, HomeResult> {

    override fun invoke(action: DeleteCompletedTasksAction) = flow {
        emit(InProgressResult)

        val doneTasks = getAllDoneTasksUseCase()
        deleteTasksUseCase(doneTasks)
        emit(DeleteCompletedTasksResult(doneTasks))
    }.catch {
        emit(ErrorResult(it))
    }
}
