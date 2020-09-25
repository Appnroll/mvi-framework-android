package com.appnroll.mvi.common.mvi

import com.appnroll.mvi.common.mvi.internal.MviViewStateCache
import com.appnroll.mvi.common.mvi.model.MviAction
import com.appnroll.mvi.common.mvi.model.MviActionProcessingFlow
import com.appnroll.mvi.common.mvi.model.MviResult
import com.appnroll.mvi.common.mvi.state.MviStateProcessingFlow
import com.appnroll.mvi.common.mvi.state.MviViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

open class MviController<A : MviAction, R : MviResult, VS : MviViewState>(
    private val mviActionProcessingFlow: MviActionProcessingFlow<A, R>,
    private val mviStateProcessingFlow: MviStateProcessingFlow<R, VS>,
    private val mviViewStateCache: MviViewStateCache<VS>,
    private val coroutineScope: CoroutineScope
) {
    val viewStatesFlow: Flow<VS> = mviStateProcessingFlow

   init {
        mviActionProcessingFlow.onEach { mviStateProcessingFlow.accept(it) }.launchIn(coroutineScope)
        mviStateProcessingFlow.savable
            .onEach { mviViewStateCache.set(it) }
            .launchIn(coroutineScope)
    }

    fun accept(intent: VS.() -> A?) {
        coroutineScope.launch {
            mviActionProcessingFlow.accept(
                action = intent(mviStateProcessingFlow.current()) ?: return@launch
            )
        }
    }
}