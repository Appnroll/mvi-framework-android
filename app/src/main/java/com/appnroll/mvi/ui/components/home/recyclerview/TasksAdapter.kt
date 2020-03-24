package com.appnroll.mvi.ui.components.home.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.appnroll.mvi.ui.model.Task


class TasksAdapter: RecyclerView.Adapter<TaskViewHolder>() {

    var onCheckChangeListener: ((Long, Boolean) -> Unit)? = null

    private var tasks = emptyList<Task>()

    fun updateList(newTasks: List<Task>) {
        val diffResult = DiffUtil.calculateDiff(TasksDiffCallback(tasks, newTasks))
        diffResult.dispatchUpdatesTo(this)
        this.tasks = newTasks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(parent)
    }

    override fun getItemCount() = tasks.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position], ::onCheckChanged)
    }

    private fun onCheckChanged(taskId: Long, isChecked: Boolean) {
        onCheckChangeListener?.invoke(taskId, isChecked)
    }
}
