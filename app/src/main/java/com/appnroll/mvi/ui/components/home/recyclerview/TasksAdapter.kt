package com.appnroll.mvi.ui.components.home.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appnroll.mvi.utils.update

class TasksAdapter(
    private val onCheckChangeListener: ((Long, Boolean) -> Unit)
) : RecyclerView.Adapter<TaskViewHolder>() {
    var tasks by update(::TasksDiffCallback)

    override fun getItemCount() = tasks.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        return TaskViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position], onCheckChangeListener)
    }
}
