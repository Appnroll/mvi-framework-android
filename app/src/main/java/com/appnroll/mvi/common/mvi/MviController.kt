package com.appnroll.mvi.common.mvi

import com.appnroll.mvi.common.mvi.api.MviActionProcessor
import com.appnroll.mvi.common.mvi.processing.MviActionProcessing
import com.appnroll.mvi.common.mvi.processing.MviResultProcessing
import com.appnroll.mvi.common.mvi.api.MviViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/**
 * Wrapper around whole Mvi flow: MviAction -> processing -> MviResult -> reducing -> MviViewState
 *
 * MviAction's are consumed with `fun accept(...)` and flow with MviViewStates is available with
 * the property `val viewStatesFlow`
 *
 * @param mviActionProcessing
 * @param mviResultProcessing
 * @param coroutineScope
 */
open class MviController<VS : MviViewState>(
    private val mviActionProcessing: MviActionProcessing<VS>,
    private val mviResultProcessing: MviResultProcessing<VS>,
    private val coroutineScope: CoroutineScope
) {
    val viewStatesFlow: Flow<VS> = mviResultProcessing.viewStatesFlow

    init {
        mviActionProcessing.resultsFlow
            .onEach { mviResultProcessing.accept(it) }
            .launchIn(coroutineScope)

        mviResultProcessing.savableOutput.launchIn(coroutineScope)
    }

    protected fun currentViewState(): VS {
        return mviResultProcessing.currentViewState()
    }

    fun accept(processor: MviActionProcessor<VS>) {
        coroutineScope.launch {
            mviActionProcessing.accept(processor)
        }
    }
}