@file:Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")

package com.appnroll.mvi.data.usecases

import com.appnroll.mvi.common.SuspendableUseCase0
import com.appnroll.mvi.common.SuspendableUseCase1
import com.appnroll.mvi.common.SuspendableUseCase1n
import com.appnroll.mvi.data.room.dao.TaskDao
import com.appnroll.mvi.model.Task
import com.appnroll.mvi.model.toTask
import com.appnroll.mvi.model.toTaskEntity

interface AddTaskUseCase : SuspendableUseCase1<Task, Task>
interface GetTaskUseCase : SuspendableUseCase1<Long, Task?>
interface GetAllTasksUseCase : SuspendableUseCase0<List<Task>>
interface GetAllDoneTasksUseCase : SuspendableUseCase0<List<Task>>
interface UpdateTaskUseCase : SuspendableUseCase1n<Task>
interface DeleteTasksUseCase : SuspendableUseCase1n<List<Task>>

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
) : GetTaskUseCase {

    override suspend fun invoke(taskId: Long): Task? {
        return taskDao
            .getForId(taskId)
            .firstOrNull()
            ?.toTask()
    }
}

class GetAllTasksUseCaseImpl(
    private val taskDao: TaskDao
) : GetAllTasksUseCase {

    override suspend fun invoke(): List<Task> {
        return taskDao
            .getAll()
            .map { it.toTask() }
    }
}

class GetAllDoneTasksUseCaseImpl(
    private val taskDao: TaskDao
) : GetAllDoneTasksUseCase {

    override suspend fun invoke(): List<Task> {
        return taskDao
            .getAllDone()
            .map { it.toTask() }
    }
}

class UpdateTaskUseCaseImpl(
    private val taskDao: TaskDao
) : UpdateTaskUseCase {

    override suspend fun invoke(task: Task) {
        taskDao.update(task.toTaskEntity())
    }
}

class DeleteTasksUseCaseImpl(
    private val taskDao: TaskDao
) : DeleteTasksUseCase {

    override suspend fun invoke(tasks: List<Task>) {
        taskDao
            .delete(tasks.map { it.toTaskEntity() })
    }
}