package com.appnroll.mvi.ui.components.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appnroll.mvi.R
import com.appnroll.mvi.ui.base.mvi.MviStateController
import com.appnroll.mvi.utils.mviStateProcessor
import com.appnroll.mvi.ui.components.home.mvi.HomeAction
import com.appnroll.mvi.ui.components.home.recyclerview.TasksAdapter
import com.appnroll.mvi.utils.whenStarted
import com.appnroll.mvi.utils.whenStopped
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {
    private val homeStateController: MviStateController<HomeAction, HomeViewState> by mviStateProcessor(ViewModelName)

    private val tasksAdapter = TasksAdapter { taskId, isChecked ->
        homeStateController.accept { updateTask(taskId, isChecked) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        whenStarted {
            val disposable = homeStateController.init(::render)
            whenStopped {
                disposable.dispose()
            }

            homeStateController.accept { loadDataIfNeeded() }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTasksRecyclerView()

        addTaskButton.setOnClickListener {
            homeStateController.accept { addTask(newTaskInput.text.toString()) }
        }

        deleteCompletedTasksButton.setOnClickListener {
            homeStateController.accept { deleteCompletedTasks() }
        }
    }

    private fun render(viewState: HomeViewState) {
        with(viewState) {
            progressBar.isVisible = inProgress
            newTaskInput.isEnabled = !inProgress
            addTaskButton.isEnabled = !inProgress

            deleteCompletedTasksButton.isEnabled = tasks?.find { it.isDone } != null

            viewState.tasks?.let { newTasks ->
                tasksAdapter.tasks = newTasks
            }

            newTaskAdded?.consume {
                newTaskInput.setText("")
            }

            error?.consume {
                Toast.makeText(requireContext(), "Error occurred", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initTasksRecyclerView() {
        tasksRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        tasksRecyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        tasksRecyclerView.adapter = tasksAdapter
    }

    companion object {
        const val ViewModelName = "viewmodel.home"
    }
}