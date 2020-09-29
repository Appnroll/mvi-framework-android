package com.appnroll.mvi.common.mvi

import com.appnroll.mvi.common.mvi.api.MviViewStateCache
import com.appnroll.mvi.common.mvi.api.MviAction
import com.appnroll.mvi.common.mvi.processing.MviActionProcessing
import com.appnroll.mvi.common.mvi.api.MviResult
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
 * @param mviViewStateCache
 * @param coroutineScope
 */
open class MviController<A : MviAction, R : MviResult, VS : MviViewState>(
    private val mviActionProcessing: MviActionProcessing<A, R>,
    private val mviResultProcessing: MviResultProcessing<R, VS>,
    private val mviViewStateCache: MviViewStateCache<VS>,
    private val coroutineScope: CoroutineScope
) {
    val viewStatesFlow: Flow<VS> = mviResultProcessing.viewStatesFlow

    init {
        mviActionProcessing.resultsFlow
            .onEach { mviResultProcessing.accept(it) }
            .launchIn(coroutineScope)

        mviResultProcessing.savableViewStatesFlow
            .onEach { mviViewStateCache.set(it) }
            .launchIn(coroutineScope)
    }

    fun accept(intent: VS.() -> A?) {
        coroutineScope.launch {
            mviActionProcessing.accept(
                action = intent(mviResultProcessing.currentViewState()) ?: return@launch
            )
        }
    }
}