package com.appnroll.mvi.data.repositories

import com.appnroll.mvi.data.room.dao.TaskDao
import com.appnroll.mvi.ui.model.Task
import com.appnroll.mvi.ui.model.toTask
import com.appnroll.mvi.ui.model.toTaskEntity


class TaskRepositoryImpl(
    private val taskDao: TaskDao
): TaskRepository {

    override fun addTask(task: Task): Task {
        val id = taskDao
            .insert(task.toTaskEntity())
        return task.copy(id = id)
    }

    override fun getTask(taskId: Long): Task? {
        return taskDao
            .getForId(taskId)
            .firstOrNull()
            ?.toTask()
    }

    override fun getAllTasks(): List<Task> {
        return taskDao
            .getAll()
            .map { it.toTask() }
    }

    override fun getAllDoneTasks(): List<Task> {
        return taskDao
            .getAllDone()
            .map { it.toTask() }
    }

    override fun updateTask(task: Task) {
        taskDao.update(task.toTaskEntity())
    }

    override fun delete(tasks: List<Task>) {
        taskDao
            .delete(tasks.map { it.toTaskEntity() })
    }
}