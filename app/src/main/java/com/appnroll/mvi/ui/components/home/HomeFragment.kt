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
import com.appnroll.mvi.ui.components.home.mvi.HomeViewModel
import com.appnroll.mvi.ui.components.home.mvi.state.HomeViewState
import com.appnroll.mvi.ui.components.home.recyclerview.TasksAdapter
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by stateViewModel()
    private val homeMviController by lazy { homeViewModel.homeMviController }

    private val tasksAdapter = TasksAdapter { taskId, isChecked ->
        homeMviController.accept { updateTask(taskId, isChecked) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeMviController.apply {
            state.asLiveData().observe(viewLifecycleOwner, ::render)
            accept { loadDataIfNeeded() }
        }

        tasksRecyclerView.adapter = tasksAdapter

        addTaskButton.setOnClickListener {
            homeMviController.accept { addTask(newTaskInput.text.toString()) }
        }

        deleteCompletedTasksButton.setOnClickListener {
            homeMviController.accept { deleteCompletedTasks() }
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