package com.appnroll.mvi.data.repositories

import com.appnroll.mvi.ui.model.Task


interface TaskRepository {

    fun addTask(task: Task): Task

    fun getTask(taskId: Long): Task?

    fun getAllTasks(): List<Task>

    fun getAllDoneTasks(): List<Task>

    fun updateTask(task: Task)

    fun delete(tasks: List<Task>)
}