package com.appnroll.mvi.ui.components.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import com.appnroll.mvi.R
import com.appnroll.mvi.ui.components.home.mvi.HomeFlowController
import com.appnroll.mvi.ui.components.home.mvi.state.HomeViewState
import com.appnroll.mvi.ui.components.home.recyclerview.TasksAdapter
import com.appnroll.mvi.utils.mviController
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment() {
    private val homeFlowController by mviController<HomeFlowController>()

    private val tasksAdapter = TasksAdapter { taskId, isChecked ->
        homeFlowController.accept { updateTask(taskId, isChecked) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeFlowController.apply {
            state.asLiveData().observe(viewLifecycleOwner, ::render)
            homeFlowController.accept { loadDataIfNeeded() }
        }

        tasksRecyclerView.adapter = tasksAdapter

        addTaskButton.setOnClickListener {
            homeFlowController.accept { addTask(newTaskInput.text.toString()) }
        }

        deleteCompletedTasksButton.setOnClickListener {
            homeFlowController.accept { deleteCompletedTasks() }
        }
    }

    private fun render(viewState: HomeViewState) {
        with(viewState) {
            progressBar.isVisible = inProgress
            newTaskInput.isEnabled = !inProgress
            addTaskButton.isEnabled = !inProgress

            deleteCompletedTasksButton.isEnabled = tasks.orEmpty().any { it.isDone }

            tasks?.let { newTasks ->
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
}