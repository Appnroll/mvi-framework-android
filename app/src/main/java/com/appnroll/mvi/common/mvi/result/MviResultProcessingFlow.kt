package com.appnroll.mvi.common.mvi.result

import com.appnroll.mvi.common.mvi.viewstate.MviViewStateCache
import com.appnroll.mvi.common.mvi.viewstate.MviViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull

@Suppress("EXPERIMENTAL_API_USAGE")
open class MviResultProcessingFlow<R : MviResult, VS : MviViewState>(
    mviViewStateCache: MviViewStateCache<VS>,
    private val mviResultReducer: MviResultReducer<R, VS>,
    private val stateFlow: MutableStateFlow<VS> = MutableStateFlow(value = mviViewStateCache.get() ?: mviResultReducer.default())
) : Flow<VS> by stateFlow {

    val savable: Flow<VS> = stateFlow
        .filter { it.isSavable }
        .mapNotNull { mviResultReducer.fold(it) }

    fun accept(result: R) {
        stateFlow.value = with(mviResultReducer) { current().reduce(result) }
    }

    fun current(): VS = stateFlow.value
}
