package com.appnroll.mvi.common.mvi.processing

import com.appnroll.mvi.common.mvi.api.MviResult
import com.appnroll.mvi.common.mvi.api.MviResultReducer
import com.appnroll.mvi.common.mvi.tools.MviViewStateCache
import com.appnroll.mvi.common.mvi.api.MviViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull

@Suppress("EXPERIMENTAL_API_USAGE")
open class MviResultProcessing<R : MviResult, VS : MviViewState>(
    mviViewStateCache: MviViewStateCache<VS>,
    private val mviResultReducer: MviResultReducer<R, VS>
) {
    private val output = MutableStateFlow(value = mviViewStateCache.get() ?: mviResultReducer.default())

    private val savableOutput = output
        .filter { it.isSavable }
        .mapNotNull { mviResultReducer.fold(it) }

    val viewStatesFlow: Flow<VS> = output
    val savableViewStatesFlow: Flow<VS> = savableOutput

    fun accept(result: R) {
        output.value = with(mviResultReducer) { currentViewState().reduce(result) }
    }

    fun currentViewState(): VS = output.value
}
