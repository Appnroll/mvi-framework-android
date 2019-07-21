package com.appnroll.mvi.ui.base.mvi

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable


abstract class MviViewModel<A: MviAction, R: MviResult, VS: MviViewState<R>>(
    private val savedStateHandle : SavedStateHandle,
    private val actionProcessor: ObservableTransformer<A, R>,
    defaultViewState: VS
): ViewModel() {

    var viewState: VS = savedStateHandle.get<VS>(VIEW_STATE_KEY) ?: defaultViewState
    
    private val compositeDisposable = CompositeDisposable()
    private val actionsObserver = PublishRelay.create<A>()
    private val actionsSource = PublishRelay.create<A>()
    
    val viewStatesObservable: Observable<VS> by lazy {
        actionsObserver
            .compose(actionProcessor)
            .scan(viewState, ::reduce)
            .doOnNext(::save)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)
    }
    
    fun startProcessingActions() {
        actionsSource.subscribe(actionsObserver).run { compositeDisposable.add(this) }
        initialActions().forEach { action -> accept(action) }
    }
    
    fun stopProcessingActions() {
        compositeDisposable.clear()
    }

    fun accept(action: A) {
        actionsSource.accept(action)
    }
    
    /**
     * Lifecycle can be used for tracking Fragment or Activity events from within ViewModel
     */
    open fun onLifecycleAttached(lifecycle: Lifecycle) = Unit
    
    /**
     * List of actions which will be invoked immediately after onStart callback in Activity or Fragment
     */
    open fun initialActions(): List<A> = emptyList()
    
    /**
     * Transform viewState when saving it to the handle which is restored after view model process recreation
     */
    open fun onSaveViewState(viewState: VS): VS? = viewState

    @Suppress("UNCHECKED_CAST")
    private fun reduce( viewState: VS, result: R): VS {
        return viewState.reduce(result) as VS
    }
    
    private fun save(newViewState: VS) {
        if (newViewState.isSavable()) {
            savedStateHandle.set(VIEW_STATE_KEY, onSaveViewState(newViewState))
            viewState = newViewState
        }
    }
    
    companion object {
        private const val VIEW_STATE_KEY = "ViewStateKey"
    }
}