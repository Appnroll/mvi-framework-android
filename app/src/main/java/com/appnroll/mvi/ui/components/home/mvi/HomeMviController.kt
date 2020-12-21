package com.appnroll.mvi.ui.components.home.mvi

import androidx.lifecycle.SavedStateHandle
import com.appnroll.mvi.common.mvi.MviController
import com.appnroll.mvi.common.MviViewStateCacheAndroid
import com.appnroll.mvi.common.mvi.api.MviActionProcessor
import com.appnroll.mvi.common.mvi.processing.MviActionProcessing
import com.appnroll.mvi.common.mvi.processing.MviResultProcessing
import com.appnroll.mvi.data.usecases.AddTaskUseCase
import com.appnroll.mvi.data.usecases.DeleteTasksUseCase
import com.appnroll.mvi.data.usecases.GetAllDoneTasksUseCase
import com.appnroll.mvi.data.usecases.GetAllTasksUseCase
import com.appnroll.mvi.data.usecases.GetTaskUseCase
import com.appnroll.mvi.data.usecases.UpdateTaskUseCase
import com.appnroll.mvi.model.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * This is a kind of boilerplate code which is written in order to provide a strong types for all
 * mvi related classes from one feature. It is done in order to simplify dependency injection which
 * in case of Koin relies on class types.
 *
 * An alternative solution is to use Koin named() qualifiers, in example:
 * ```
 * factory(named("HomeActionProcessor")) {
 *     HomeActionProcessor(...)
 * }
 *
 * factory(named("HomeActionProcessingFLow")) {
 *     MviActionProcessingFlow<MviAction, MviResult>(
 *         mviActionProcessor = get(named("HomeActionProcessor"))
 *     )
 * }
 *
 * factory(named("HomeMviController")) { (...) ->
 *     MviController<MviAction, MviResult, MviViewState>(
 *         mviActionProcessingFlow = get(named("HomeActionProcessingFLow")),
 *         ...
 *     )
 * }
 * ```
 * However (in my opinion) providing strong type is less error prone then named() qualifiers.
 */

/** delay processing of na action - only for test purposes */
private const val PROCESSING_DELAY_MILS = 2000L

class HomeMviController(
    val homeActionProcessing: HomeActionProcessing,
    homeResultProcessing: HomeResultProcessing,
    coroutineScope: CoroutineScope,
    private val getAllTasksUseCase: GetAllTasksUseCase,
    private val addTaskUseCase: AddTaskUseCase,
    private val getTaskUseCase: GetTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase,
    private val getAllDoneTasksUseCase: GetAllDoneTasksUseCase,
    private val deleteTasksUseCase: DeleteTasksUseCase
) : MviController<HomeViewState>(
    mviActionProcessing = homeActionProcessing,
    mviResultProcessing = homeResultProcessing,
    coroutineScope = coroutineScope
) {

    fun loadDataIfNeeded() {
        accept(object : MviActionProcessor<HomeViewState> {

            override fun invoke() = flow<HomeViewState.() -> HomeViewState> {
                emit { inProgress() }
                delay(PROCESSING_DELAY_MILS)
                val tasks = getAllTasksUseCase()
                emit { tasksLoaded(tasks) }
            }.catch {
                emit { error(it) }
            }
        })
    }

    fun addTask(taskContent: String) {
        accept(object : MviActionProcessor<HomeViewState> {

            override fun invoke() = flow<HomeViewState.() -> HomeViewState> {
                emit { inProgress() }
                delay(PROCESSING_DELAY_MILS)
                val newTask = addTaskUseCase(Task(0, taskContent, false))
                emit { taskAdded(newTask) }
            }.catch {
                emit { error(it) }
            }
        })
    }

    fun deleteCompletedTasks() {
        accept(object : MviActionProcessor<HomeViewState> {

            override fun invoke() = flow<HomeViewState.() -> HomeViewState> {
                emit { inProgress() }
                delay(PROCESSING_DELAY_MILS)
                val doneTasks = getAllDoneTasksUseCase()
                deleteTasksUseCase(doneTasks)
                emit { tasksDeleted(doneTasks) }
            }.catch {
                emit { error(it) }
            }
        })
    }

    fun updateTask(taskId: Long, isDone: Boolean) {
        accept(object : MviActionProcessor<HomeViewState> {

            override fun invoke() = flow<HomeViewState.() -> HomeViewState> {
                emit { inProgress() }
                delay(PROCESSING_DELAY_MILS)
                val task = getTaskUseCase(taskId)?.copy(isDone = isDone)
                if (task == null) {
                    emit { error(Exception("Task with id $taskId not found in DB")) }
                } else {
                    updateTaskUseCase(task)
                    emit { taskUpdated(task) }
                }
            }.catch {
                emit { error(it) }
            }

            override fun getId() = super.getId().toString() + taskId
        })
    }
}

class HomeActionProcessing : MviActionProcessing<HomeViewState>()

class HomeResultProcessing(
    homeResultReducer: HomeResultReducer,
    homeViewStateCache: HomeViewStateCache
) : MviResultProcessing<HomeViewState>(
    mviViewStateCache = homeViewStateCache,
    mviResultReducer = homeResultReducer
)

class HomeViewStateCache(
    savedStateHandle: SavedStateHandle
) : MviViewStateCacheAndroid<HomeViewState>(
    key = "HomeViewStateKey",
    savedStateHandle = savedStateHandle
)
