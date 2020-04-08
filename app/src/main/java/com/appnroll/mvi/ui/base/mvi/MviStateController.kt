package com.appnroll.mvi.ui.base.mvi

import io.reactivex.disposables.Disposable

interface MviStateController<A : MviAction, VS : MviViewState> {
    fun init(render: (VS) -> Unit): Disposable
    fun accept(intent: VS.() -> A?)
}

inline val <A : MviAction, VS : MviViewState> MviViewModel<A, *, VS>.stateController
    get() = this as MviStateController<A, VS>
