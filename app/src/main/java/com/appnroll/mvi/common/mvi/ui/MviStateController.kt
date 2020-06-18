package com.appnroll.mvi.common.mvi.ui

import com.appnroll.mvi.common.mvi.MviController
import com.appnroll.mvi.common.mvi.model.MviResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlin.reflect.KClass

/*
* Logic Controller
* */
interface MviStateController<T, R> : MviController<T, R> {
    val current: R
    val savable: Flow<R>
}

@Suppress("EXPERIMENTAL_API_USAGE")
fun <R : MviResult, S : MviViewState> stateControllerOf(
    @Suppress("UNUSED_PARAMETER") resultFamily: KClass<R>,
    initial: S?,
    reducer: MviReducer<R, S>
): MviStateController<R, S> {
    val stateFlow = MutableStateFlow(initial ?: reducer.default())

    return object : MviStateController<R, S>, Flow<S> by stateFlow {
        override val current: S
            get() = stateFlow.value

        override val savable: Flow<S>
            get() = stateFlow
                .filter { it.isSavable }
                .mapNotNull { reducer.fold(it) }

        override suspend fun accept(value: R) {
            stateFlow.value = with(reducer) { current.reduce(value) }
        }
    }
}

inline fun <reified R : MviResult, S : MviViewState> stateControllerOf(
    initial: S?,
    reducer: MviReducer<R, S>
): MviStateController<R, S> =
    stateControllerOf(R::class, initial, reducer)