package com.appnroll.mvi.common.mvi.state

import com.appnroll.mvi.common.mvi.internal.MviViewStateCache
import com.appnroll.mvi.common.mvi.model.MviResult
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull

@Suppress("EXPERIMENTAL_API_USAGE")
open class MviStateProcessingFlow<R : MviResult, VS : MviViewState>(
    mviViewStateCache: MviViewStateCache<VS>,
    private val mviStateReducer: MviStateReducer<R, VS>,
    private val stateFlow: MutableStateFlow<VS> = MutableStateFlow(value = mviViewStateCache.get() ?: mviStateReducer.default())
) : Flow<VS> by stateFlow {

    val savable: Flow<VS> = stateFlow
        .filter { it.isSavable }
        .mapNotNull { mviStateReducer.fold(it) }

    fun accept(result: R) {
        stateFlow.value = with(mviStateReducer) { current().reduce(result) }
    }

    fun current(): VS = stateFlow.value
}
