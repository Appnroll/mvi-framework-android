package com.appnroll.mvi.ui.components.home.mvi.model

import com.appnroll.mvi.common.mvi.model.MviActionProcessor
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
import com.appnroll.mvi.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * MviActionProcessor which delegates processing of each action to "specialized" processor.
 * This is only the conventions and it could be implemented differently.
 *
 * "specialized" processors have the same interface as main processor and they are extracted
 * to separate classes because each of them has different dependencies and it is responsible
 * for processing different action.
 */
class HomeActionProcessor(
    private val loadTasksActionProcessor: LoadTasksActionProcessor,
    private val addTaskActionProcessor: AddTaskActionProcessor,
    private val updateTaskActionProcessor: UpdateTaskActionProcessor,
    private val deleteCompletedTasksActionProcessor: DeleteCompletedTasksActionProcessor
) : MviActionProcessor<HomeAction, HomeResult> {

    override fun invoke(action: HomeAction): Flow<HomeResult> {
        return when (action) {
            is LoadTasksAction -> loadTasksActionProcessor(action)
            is AddTaskAction -> addTaskActionProcessor(action)
            is UpdateTaskAction -> updateTaskActionProcessor(action)
            is DeleteCompletedTasksAction -> deleteCompletedTasksActionProcessor(action)
        }
    }
}

/**
 * "Specialized" processors. Each of them is responsible for processing different action
 * and each of them depends on the UseCases. This is also a convention and it could be implemented
 * differently. For example, processors could depends on repositories or services.
 */

class LoadTasksActionProcessor(
    private val getAllTasksUseCase: GetAllTasksUseCase
) : MviActionProcessor<LoadTasksAction, HomeResult> {

    override fun invoke(action: LoadTasksAction) = flow {
        emit(InProgressResult)
        emit(LoadTasksResult(getAllTasksUseCase()))
    }.catch {
        emit(ErrorResult(it))
    }
}

class AddTaskActionProcessor(
    private val addTaskUseCase: AddTaskUseCase
) : MviActionProcessor<AddTaskAction, HomeResult> {

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
) : MviActionProcessor<UpdateTaskAction, HomeResult> {

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
) : MviActionProcessor<DeleteCompletedTasksAction, HomeResult> {

    override fun invoke(action: DeleteCompletedTasksAction) = flow {
        emit(InProgressResult)

        val doneTasks = getAllDoneTasksUseCase()
        deleteTasksUseCase(doneTasks)
        emit(DeleteCompletedTasksResult(doneTasks))
    }.catch {
        emit(ErrorResult(it))
    }
}
