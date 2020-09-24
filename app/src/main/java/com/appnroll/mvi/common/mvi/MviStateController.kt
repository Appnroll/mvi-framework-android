package com.appnroll.mvi.common.mvi

import com.appnroll.mvi.common.mvi.model.MviResult
import com.appnroll.mvi.common.mvi.state.MviStateReducer
import com.appnroll.mvi.common.mvi.state.MviViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull

/*
* Logic Controller
* */
@Deprecated("Use MviStateProcessingFlow")
interface MviStateController<R, S> : Flow<S> {
    val current: S
    val savable: Flow<S>

    suspend fun accept(result: R)
}

@Deprecated("Use MviStateProcessingFlow")
@Suppress("EXPERIMENTAL_API_USAGE")
fun <R : MviResult, S : MviViewState> stateControllerOfOld(
    initial: S?,
    stateReducer: MviStateReducer<R, S>
): MviStateController<R, S> {
    val stateFlow = MutableStateFlow(initial ?: stateReducer.default())

    return object : MviStateController<R, S>, Flow<S> by stateFlow {
        override val current: S
            get() = stateFlow.value

        override val savable: Flow<S>
            get() = stateFlow
                .filter { it.isSavable }
                .mapNotNull { stateReducer.fold(it) }

        override suspend fun accept(result: R) {
            stateFlow.value = with(stateReducer) { current.reduce(result) }
        }
    }
}
/*

inline fun <reified R : MviResult, S : MviViewState> stateControllerOf(
    initial: S?,
    stateReducer: MviStateReducer<R, S>
): MviStateProcessingFlow<R, S> =
    stateControllerOf(R::class, initial, stateReducer)*/