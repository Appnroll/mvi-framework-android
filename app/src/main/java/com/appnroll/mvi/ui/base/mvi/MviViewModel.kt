package com.appnroll.mvi.ui.base.mvi

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable


abstract class MviViewModel<A: MviAction, R: MviResult, VS: MviViewState<R>>(
    private val savedStateHandle: SavedStateHandle,
    actionProcessor: ObservableTransformer<A, R>,
    defaultViewState: VS
): ViewModel() {

    var viewState: VS = savedStateHandle.get<VS>(VIEW_STATE_KEY) ?: defaultViewState

    private val actionsObserver = PublishRelay.create<A>()
    private val actionsSource = PublishRelay.create<A>()
    private val disposable = actionsSource.subscribe(actionsObserver)

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    private val viewStatesObservable: Observable<VS> by lazy {
        actionsObserver
            .compose(actionProcessor)
            .scan(viewState, ::reduce)
            .doOnNext(::save)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }

    fun subscribe(render: (VS) -> Unit): Disposable = viewStatesObservable.subscribe(render)

    fun accept(action: A) = actionsSource.accept(action)

    @Suppress("UNCHECKED_CAST")
    private fun reduce(viewState: VS, result: R): VS {
        return viewState.reduce(result) as VS
    }

    private fun save(newViewState: VS) {
        if (newViewState.isSavable()) {
            savedStateHandle.set(VIEW_STATE_KEY, onSaveViewState(newViewState))
            viewState = newViewState
        }
    }

    /**
     * Transform viewState when saving it to the handle which is restored after view model recreation
     */
    open fun onSaveViewState(viewState: VS): VS? = viewState

    companion object {
        private const val VIEW_STATE_KEY = "ViewStateKey"
    }
}

fun <VM: MviViewModel<*, *, *>> Fragment.provide(viewModelClass: Class<VM>) =
    ViewModelProviders.of(this, SavedStateViewModelFactory(this)).get(viewModelClass)

fun <VM: MviViewModel<*, *, *>> FragmentActivity.provide(viewModelClass: Class<VM>) =
    ViewModelProviders.of(this, SavedStateViewModelFactory(this)).get(viewModelClass)
