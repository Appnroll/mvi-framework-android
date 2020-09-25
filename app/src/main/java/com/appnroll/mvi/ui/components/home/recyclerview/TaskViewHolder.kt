package com.appnroll.mvi.ui.components.home.recyclerview

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.appnroll.mvi.R
import com.appnroll.mvi.model.Task
import com.appnroll.mvi.utils.inflateItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_task.*

class TaskViewHolder(
    parent: ViewGroup,
    override val containerView: View = parent.inflateItem(R.layout.item_task)
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(item: Task, onCheckChanged: ((Long, Boolean) -> Unit)) {
        taskCheck.setOnCheckedChangeListener(null)
        taskContent.text = item.content
        taskCheck.isChecked = item.isDone
        taskCheck.setOnCheckedChangeListener { _, isChecked ->
            onCheckChanged(item.id, isChecked)
        }
    }
}
