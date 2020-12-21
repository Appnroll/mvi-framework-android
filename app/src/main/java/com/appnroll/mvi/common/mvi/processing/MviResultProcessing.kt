package com.appnroll.mvi.common.mvi.processing

import com.appnroll.mvi.common.mvi.api.MviResultReducer
import com.appnroll.mvi.common.mvi.api.MviViewStateCache
import com.appnroll.mvi.common.mvi.api.MviViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach

/**
 * Wrapper around MviResultReducer.
 *
 * It consumes MviResult's {fun accept(...)} then passes it to MviResultReducer which then reduces
 * it with the latest MviViewState and produces new MviViewState which is available outside with
 * a property `val viewStatesFlow`
 *
 * @param mviViewStateCache an implementation of cache which could hold an initial instance of
 * a MviViewState. It should be used when recreating MviResultProcessing object to provide the
 * latest MviViewState (it is better to use the latest one and not the default one)
 * @param mviResultReducer object responsible for reducing MviResults with MviViewState
 * and producing new MviViewState
 */
@Suppress("EXPERIMENTAL_API_USAGE")
open class MviResultProcessing<VS : MviViewState>(
    mviViewStateCache: MviViewStateCache<VS>,
    private val mviResultReducer: MviResultReducer<VS>
) {
    private val output = MutableStateFlow(value = mviViewStateCache.get() ?: mviResultReducer.default())

    val savableOutput = output
        .filter { it.isSavable() }
        .mapNotNull { mviResultReducer.fold(it) }
        .onEach { mviViewStateCache.set(it) }

    val viewStatesFlow: Flow<VS> = output

    fun accept(reducingFun: VS.() -> VS) {
        output.value = with(mviResultReducer) { currentViewState().reduce(reducingFun) }
    }

    fun currentViewState(): VS = output.value
}
