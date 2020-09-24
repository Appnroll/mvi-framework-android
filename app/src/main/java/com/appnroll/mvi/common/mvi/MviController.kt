package com.appnroll.mvi.common.mvi

import androidx.lifecycle.SavedStateHandle
import com.appnroll.mvi.common.mvi.model.MviAction
import com.appnroll.mvi.common.mvi.model.MviActionProcessingFlow
import com.appnroll.mvi.common.mvi.model.MviResult
import com.appnroll.mvi.common.mvi.state.MviStateProcessingFlow
import com.appnroll.mvi.common.mvi.state.MviViewState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.util.PrimitiveIterator

class MviController<A : MviAction, R : MviResult, VS : MviViewState>(
    private val mviActionProcessingFlow: MviActionProcessingFlow<A, R>,
    private val mviStateProcessingFlow: MviStateProcessingFlow<R, VS>,
    private val mviViewStateCache: MviViewStateCache<VS>
) {
    private lateinit var scope: CoroutineScope

    val state: Flow<VS> = mviStateProcessingFlow

    fun init(scope: CoroutineScope) {
        this.scope = scope
        mviActionProcessingFlow.onEach(mviStateProcessingFlow::accept).launchIn(scope)
        // TODO: should it be in mviStateProcessingFlow?
        mviStateProcessingFlow.savable
            .onEach { mviViewStateCache.set(it) }
            .launchIn(scope)
    }

    fun accept(intent: VS.() -> A?) {
        if (!::scope.isInitialized) {
            throw IllegalStateException("Coroutine cope is not initialized for MVI controller")
        }
        scope.launch {
            mviActionProcessingFlow.accept(
                action = intent(mviStateProcessingFlow.current()) ?: return@launch
            )
        }
    }
}