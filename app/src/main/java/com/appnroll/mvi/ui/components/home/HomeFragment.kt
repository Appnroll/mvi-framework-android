package com.appnroll.mvi.ui.components.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.observe
import com.appnroll.mvi.R
import com.appnroll.mvi.ui.components.home.mvi.viewstate.HomeViewState
import com.appnroll.mvi.ui.components.home.recyclerview.TasksAdapter
import com.appnroll.mvi.utils.mviController
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    /** Gets HomeViewModel instance and extract mviController from it */
    private val homeMviController by mviController(HomeViewModel::class) { homeMviController }

    private val tasksAdapter = TasksAdapter { taskId, isChecked ->
        /** send action to change tasks state (completed/not completed) */
        homeMviController.accept { updateTask(taskId, isChecked) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tasksRecyclerView.adapter = tasksAdapter

        initButtons()

        /** subscribe with a render function to LifeData with viewStates */
        homeMviController.viewStatesFlow.asLiveData().observe(viewLifecycleOwner, ::render)
    }

    override fun onStart() {
        super.onStart()
        /** send action to load tasks list if list is empty */
        homeMviController.accept { loadDataIfNeeded() }
    }

    private fun initButtons() {
        addTaskButton.setOnClickListener {
            /** send action to add new task */
            homeMviController.accept { addTask(newTaskInput.text.toString()) }
        }

        deleteCompletedTasksButton.setOnClickListener {
            /** send action to delete all completed tasks */
            homeMviController.accept { deleteCompletedTasks() }
        }
    }

    /** Function which updates UI based on new viewState object receives from MviController */
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