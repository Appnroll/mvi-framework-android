package com.appnroll.mvi.common.mvi.processing

import com.appnroll.mvi.common.mvi.api.MviResult
import com.appnroll.mvi.common.mvi.api.MviResultReducer
import com.appnroll.mvi.common.mvi.utils.MviViewStateCache
import com.appnroll.mvi.common.mvi.api.MviViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull

@Suppress("EXPERIMENTAL_API_USAGE")
open class MviResultProcessingFlow<R : MviResult, VS : MviViewState> private constructor(
    private val mviResultReducer: MviResultReducer<R, VS>,
    private val stateFlow: MutableStateFlow<VS>
) : Flow<VS> by stateFlow {

    constructor(
        mviViewStateCache: MviViewStateCache<VS>,
        mviResultReducer: MviResultReducer<R, VS>
    ): this(
        mviResultReducer = mviResultReducer,
        stateFlow = MutableStateFlow(value = mviViewStateCache.get() ?: mviResultReducer.default())
    )

    val savable: Flow<VS> = stateFlow
        .filter { it.isSavable }
        .mapNotNull { mviResultReducer.fold(it) }

    fun accept(result: R) {
        stateFlow.value = with(mviResultReducer) { current().reduce(result) }
    }

    fun current(): VS = stateFlow.value
}
