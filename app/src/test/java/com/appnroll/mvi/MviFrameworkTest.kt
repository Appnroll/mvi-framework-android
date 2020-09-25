@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.appnroll.mvi

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.appnroll.mvi.TestAction.TestAction1
import com.appnroll.mvi.TestAction.TestAction2
import com.appnroll.mvi.TestResult.TestResult1
import com.appnroll.mvi.TestResult.TestResult2
import com.appnroll.mvi.TestResult.TestResult3
import com.appnroll.mvi.common.mvi.MviController
import com.appnroll.mvi.common.mvi.api.MviAction
import com.appnroll.mvi.common.mvi.api.MviResult
import com.appnroll.mvi.common.mvi.api.mviProcessor
import com.appnroll.mvi.common.mvi.modelControllerOf
import com.appnroll.mvi.common.mvi.state.stateControllerOf
import com.appnroll.mvi.common.mvi.api.MviResultReducer
import com.appnroll.mvi.common.mvi.api.MviViewState
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

//
/**
 * - render moze dropowac do ostatniego viewstate - ^ - tbc
 * - action channel moze wykonywac wiele akcji na wielu procesorach - ^ - tbc
 * - dwa procesory nigdy nie moga pracowac nad ta sama akcja - ^ - tbc
 * - result channel nie dropuje i jest tylko jeden - ^ - tbc
 * */

/**
 * TODO: test should contain:
 *  - parallel execution tests
 *  - different scopes and dispatchers tests (TBD)
 *  - cancellation tests
 *  - flow emission on different levels
 *  - asProcessingFlow tests (mainly choke -> multi -> choke pattern)
 * */

sealed class TestAction : MviAction {
    object TestAction1 : TestAction()
    object TestAction2 : TestAction()

    override fun toString(): String {
        return javaClass.simpleName
    }
}

sealed class TestResult : MviResult {
    object TestResult1 : TestResult()
    object TestResult2 : TestResult()
    object TestResult3 : TestResult()

    override fun toString(): String {
        return javaClass.simpleName
    }
}

@Parcelize
data class TestViewState(
    val results: List<String> = emptyList(),
    override val isSavable: Boolean = true
) : MviViewState {

    fun testAction1() = TestAction1
    fun testAction2() = TestAction2
}

class TestController(
    savedStateHandle: SavedStateHandle
) : MviController<TestAction, TestResult, TestViewState>(
    savedStateHandle = savedStateHandle,
    modelController = modelControllerOf { action: TestAction ->
        // processors kept in place for clarity
        val logicProcessorA =
            mviProcessor { action1: TestAction1 ->
                flow { delay(200); emit(TestResult1) }
            }

        val logicProcessorB =
            mviProcessor { action2: TestAction2 ->
                flow {
                    delay(100); emit(TestResult2)
                    delay(200); emit(TestResult3)
                }
            }

        when (action) {
            is TestAction1 -> logicProcessorA(action)
            is TestAction2 -> logicProcessorB(action)
        }
    },
    stateProcessingFlow = { savedState ->
        stateControllerOf(
            initial = savedState,
            stateReducer = object :
                MviResultReducer<TestResult, TestViewState> {
                override fun default() = TestViewState()

                override fun TestViewState.reduce(result: TestResult): TestViewState {
                    return when (result) {
                        TestResult1 -> copy(results = results + result.toString())
                        TestResult2 -> copy(results = results + result.toString())
                        TestResult3 -> copy(results = results + result.toString())
                    }
                }
            }
        )
    }
)

class MviFrameworkTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var singleThreadExecutor: ExecutorService

    @Before
    fun setUp() {
        singleThreadExecutor = Executors.newSingleThreadExecutor()
        Dispatchers.setMain(singleThreadExecutor.asCoroutineDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        singleThreadExecutor.shutdownNow()
    }

    @Test
    fun test3() {
        val mviFlowController = TestController(SavedStateHandle())

        runBlocking {
            launch(Dispatchers.Default) {
                repeat(1000) {
                    launch { delay(1); mviFlowController.accept { testAction1() } }
                }
                repeat(1000) {
                    launch { delay(1); mviFlowController.accept { testAction2() } }
                }

                mviFlowController.viewStatesFlow.asLiveData().observeForever(::println)
                mviFlowController.viewStatesFlow.asLiveData().observeForever(::println)
                mviFlowController.viewStatesFlow.asLiveData().observeForever(::println)
                mviFlowController.viewStatesFlow.asLiveData().observeForever(::println)
                mviFlowController.viewStatesFlow.asLiveData().observeForever(::println)

                delay(2500)
                cancel()
            }
        }
    }
}