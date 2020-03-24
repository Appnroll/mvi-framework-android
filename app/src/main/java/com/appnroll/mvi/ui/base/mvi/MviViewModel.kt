package com.appnroll.mvi.ui.base.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable

@Suppress("UNCHECKED_CAST")
abstract class MviViewModel<A: MviAction, R: MviResult, VS: MviViewState<R>>(
    private val savedStateHandle: SavedStateHandle,
    actionProcessor: ObservableTransformer<A, R>,
    defaultViewState: VS
): ViewModel() {

    protected var viewState: VS = savedStateHandle.get<VS>(VIEW_STATE_KEY) ?: defaultViewState

    private val actionsObserver = PublishRelay.create<A>()
    private val actionsSource = PublishRelay.create<A>()
    private val disposable = actionsSource.subscribe(actionsObserver)

    private val viewStatesObservable: Observable<VS> by lazy {
        actionsObserver
            .compose(actionProcessor)
            .scan(viewState, ::reduce)
            .distinctUntilChanged()
            .doOnNext(::save)
            .replay(1)
            .autoConnect(0)
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    fun init(render: (VS) -> Unit): Disposable = viewStatesObservable.subscribe(render)

    fun accept(action: A) = actionsSource.accept(action)

    private fun reduce(viewState: VS, result: R) = viewState.reduce(result) as VS

    private fun save(newViewState: VS) {
        if (newViewState.isSavable()) {
            savedStateHandle.set(VIEW_STATE_KEY, onSaveViewState(newViewState))
            viewState = newViewState
        }
    }

    /**
     * Transform viewState when saving it to the savedStateHandle.
     * It will then be restored after after view model recreation.
     */
    open fun onSaveViewState(viewState: VS): VS? = viewState

    companion object {
        private const val VIEW_STATE_KEY = "ViewStateKey"
    }
}