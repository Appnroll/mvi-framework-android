package com.appnroll.mvi.common.mvi.processing

import com.appnroll.mvi.common.mvi.api.MviResult
import com.appnroll.mvi.common.mvi.api.MviResultReducer
import com.appnroll.mvi.common.mvi.tools.MviViewStateCache
import com.appnroll.mvi.common.mvi.api.MviViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull

@Suppress("EXPERIMENTAL_API_USAGE")
open class MviResultProcessing<R : MviResult, VS : MviViewState>(
    mviViewStateCache: MviViewStateCache<VS>,
    private val mviResultReducer: MviResultReducer<R, VS>
) {

    private val outputFlow = MutableStateFlow(value = mviViewStateCache.get() ?: mviResultReducer.default())

    private val savableOutputFlow = outputFlow
        .filter { it.isSavable }
        .mapNotNull { mviResultReducer.fold(it) }

    fun accept(result: R) {
        outputFlow.value = with(mviResultReducer) { currentViewState().reduce(result) }
    }

    fun currentViewState(): VS = outputFlow.value

    fun viewStatesFlow() = outputFlow

    fun savableViewStatesFlow() = savableOutputFlow
}
