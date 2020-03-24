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
import com.appnroll.mvi.ui.components.home.recyclerview.TasksAdapter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class HomeFragment : Fragment() {
    private val homeViewModel: HomeViewModel by stateViewModel()
    private var onStopDisposables = CompositeDisposable()

    private val tasksAdapter = TasksAdapter { taskId, isChecked ->
        homeViewModel.updateTask(taskId, isChecked)
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
            newTaskInput.text.toString().let {
                homeViewModel.addTask(it)
            }
        }

        deleteCompletedTasksButton.setOnClickListener {
            homeViewModel.deleteCompletedTasks()
        }
    }

    override fun onStart() {
        super.onStart()
        homeViewModel.init(::render).addTo(onStopDisposables)
        homeViewModel.loadDataIfNeeded()
    }

    override fun onStop() {
        onStopDisposables.clear()
        super.onStop()
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
}