package com.appnroll.mvi.ui.base.mvi

import io.reactivex.disposables.Disposable

interface MviStateProcessor<A : MviAction, VS : MviViewState> {
    fun init(render: (VS) -> Unit): Disposable
    fun accept(intent: VS.() -> A?)
}

inline val <A : MviAction, VS : MviViewState> MviViewModel<A, *, VS>.stateReducer
    get() = this as MviStateProcessor<A, VS>
