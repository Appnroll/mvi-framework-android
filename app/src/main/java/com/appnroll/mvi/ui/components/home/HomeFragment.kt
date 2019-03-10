package com.appnroll.mvi.ui.components.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appnroll.mvi.R
import com.appnroll.mvi.ui.base.mvi.MviBaseFragment
import com.appnroll.mvi.ui.components.home.mvi.HomeAction
import com.appnroll.mvi.ui.components.home.mvi.HomeAction.*
import com.appnroll.mvi.ui.components.home.mvi.HomeResult
import com.appnroll.mvi.ui.components.home.recyclerview.TasksAdapter
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment: MviBaseFragment<HomeAction, HomeResult, HomeViewState, HomeViewModel>(HomeViewModel::class.java) {

    private val tasksAdapter = TasksAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTasksRecyclerView()

        addTaskButton.setOnClickListener {
            newTaskInput.text.toString().let {
                if (!it.isBlank()) {
                    mviController.accept(AddTaskAction(it))
                }
            }
        }

        deleteCompletedTasksButton.setOnClickListener {
            mviController.accept(DeleteCompletedTasksAction)
        }
    }

    override fun initialAction(lastViewState: HomeViewState?): HomeAction? {
        if (lastViewState?.tasks == null) {
            return LoadTasksAction
        }
        return null
    }

    override fun render(viewState: HomeViewState) {
        with(viewState) {
            progressBar.visibility = if (inProgress) View.VISIBLE else View.GONE
            newTaskInput.isEnabled = !inProgress
            addTaskButton.isEnabled = !inProgress

            deleteCompletedTasksButton.isEnabled = tasks?.find { it.isDone } != null

            viewState.tasks?.let { tasksAdapter.updateList(it) }

            newTaskAdded?.consume {
                newTaskInput.setText("")
            }

            error?.consume {
                Toast.makeText(requireContext(), "Error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initTasksRecyclerView() {
        tasksAdapter.onCheckChangeListener = { taskId, isChecked ->
            mviController.accept(UpdateTaskAction(taskId, isChecked))
        }
        tasksRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        tasksRecyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        tasksRecyclerView.adapter = tasksAdapter
    }
}