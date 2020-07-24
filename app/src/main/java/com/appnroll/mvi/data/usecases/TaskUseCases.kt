package com.appnroll.mvi.data.usecases

import com.appnroll.mvi.data.room.dao.TaskDao
import com.appnroll.mvi.ui.model.Task
import com.appnroll.mvi.ui.model.toTask
import com.appnroll.mvi.ui.model.toTaskEntity

/**
 * Interface is needed as we are not able to use suspend function as a parent
 * interface SampleUseCase: suspend () -> Unit
 */
interface SuspendableUseCase<A, R> {

    suspend operator fun invoke(argument: A): R
}

interface AddTaskUseCase: SuspendableUseCase<Task, Task>
interface GetTaskUseCase: SuspendableUseCase<Long, Task?>
interface GetAllTasksUseCase: SuspendableUseCase<Unit, List<Task>>
interface GetAllDoneTasksUseCase: SuspendableUseCase<Unit, List<Task>>
interface UpdateTaskUseCase: SuspendableUseCase<Task, Unit>
interface DeleteTasksUseCase: SuspendableUseCase<List<Task>, Unit>

class AddTaskUseCaseImpl(
    private val taskDao: TaskDao
) : AddTaskUseCase {

    override suspend fun invoke(task: Task): Task {
        val id = taskDao.insert(task.toTaskEntity())
        return task.copy(id = id)
    }
}

class GetTaskUseCaseImpl(
    private val taskDao: TaskDao
): GetTaskUseCase {

    override suspend fun invoke(taskId: Long): Task? {
        return taskDao
            .getForId(taskId)
            .firstOrNull()
            ?.toTask()
    }
}

class GetAllTasksUseCaseImpl(
    private val taskDao: TaskDao
): GetAllTasksUseCase {

    override suspend fun invoke(argument: Unit): List<Task> {
        return taskDao
            .getAll()
            .map { it.toTask() }
    }
}

class GetAllDoneTasksUseCaseImpl(
    private val taskDao: TaskDao
): GetAllDoneTasksUseCase {

    override suspend fun invoke(argument: Unit): List<Task> {
        return taskDao
            .getAllDone()
            .map { it.toTask() }
    }
}

class UpdateTaskUseCaseImpl(
    private val taskDao: TaskDao
): UpdateTaskUseCase {

    override suspend fun invoke(task: Task): Unit {
        taskDao.update(task.toTaskEntity())
    }
}

class DeleteTasksUseCaseImpl(
    private val taskDao: TaskDao
): DeleteTasksUseCase {

    override suspend fun invoke(tasks: List<Task>): Unit {
        taskDao
            .delete(tasks.map { it.toTaskEntity() })
    }
}