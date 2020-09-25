package com.appnroll.mvi.common.mvi

import com.appnroll.mvi.common.mvi.viewstate.MviViewStateCache
import com.appnroll.mvi.common.mvi.action.MviAction
import com.appnroll.mvi.common.mvi.action.MviActionProcessingFlow
import com.appnroll.mvi.common.mvi.result.MviResult
import com.appnroll.mvi.common.mvi.result.MviResultProcessingFlow
import com.appnroll.mvi.common.mvi.viewstate.MviViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

open class MviController<A : MviAction, R : MviResult, VS : MviViewState>(
    private val mviActionProcessingFlow: MviActionProcessingFlow<A, R>,
    private val mviResultProcessingFlow: MviResultProcessingFlow<R, VS>,
    private val mviViewStateCache: MviViewStateCache<VS>,
    private val coroutineScope: CoroutineScope
) {
    val viewStatesFlow: Flow<VS> = mviResultProcessingFlow

    init {
        mviActionProcessingFlow.onEach { mviResultProcessingFlow.accept(it) }
            .launchIn(coroutineScope)
        mviResultProcessingFlow.savable
            .onEach { mviViewStateCache.set(it) }
            .launchIn(coroutineScope)
    }

    fun accept(intent: VS.() -> A?) {
        coroutineScope.launch {
            mviActionProcessingFlow.accept(
                action = intent(mviResultProcessingFlow.current()) ?: return@launch
            )
        }
    }
}