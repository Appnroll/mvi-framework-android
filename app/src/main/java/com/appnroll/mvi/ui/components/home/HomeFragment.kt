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
import com.appnroll.mvi.ui.base.mvi.provide
import com.appnroll.mvi.ui.components.home.recyclerview.TasksAdapter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment: Fragment() {

    private val homeViewModel by lazy { provide(HomeViewModel::class.java) }
    private var disposables = CompositeDisposable()

    private val tasksAdapter = TasksAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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
        homeViewModel.subscribe(::render).addTo(disposables)
        homeViewModel.loadDataIfNeeded()
    }

    override fun onStop() {
        disposables.clear()
        super.onStop()
    }

    private fun render(viewState: HomeViewState) {
        with(viewState) {
            progressBar.isVisible = inProgress
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
            homeViewModel.updateTask(taskId, isChecked)
        }
        tasksRecyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        tasksRecyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        tasksRecyclerView.adapter = tasksAdapter
    }
}