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
    
    private var viewStatesObservable: Observable<VS>? = null

    @Suppress("UNCHECKED_CAST")
    fun initViewStatesObservable() {
        if (viewStatesObservable == null) {
            viewStatesObservable = actionsObserver
                .compose(actionProcessor)
                .scan(viewState) { viewState: VS, result -> viewState.reduce(result) as VS }
                .doOnNext(::saveNewViewState)
                .distinctUntilChanged()
                .replay(1)
                .autoConnect(0)
        }
    }

    fun getViewStatesObservable() =
        viewStatesObservable ?: throw Exception(
            "You need to invoke initViewStatesObservable(lastViewState) " +
                    "in onCreate() method of Your Activity or onViewCreated() method of Your Fragment"
        )
    
    fun startProcessingActions() {
        actionsSource.subscribe(actionsObserver).run { compositeDisposable.add(this) }
        initialAction()?.let { action -> accept(action) }
    }
    
    fun stopProcessingActions() {
        compositeDisposable.clear()
    }

    fun accept(action: A) {
        actionsSource.accept(action)
    }

    open fun onLifecycleAttached(lifecycle: Lifecycle) {
        // do nothing - can be override in subclasses
    }
    
    abstract fun initialAction(): A?
    
    /**
     * Transform viewState when saving it to the handle which is restored after view model process recreation
     */
    open fun onSaveViewState(viewState: VS): VS? = viewState

    private fun saveNewViewState(newViewState: VS) {
        if (newViewState.isSavable()) {
            savedStateHandle.set(VIEW_STATE_KEY, onSaveViewState(newViewState))
            viewState = newViewState
        }
    }
    
    companion object {
        private const val VIEW_STATE_KEY = "ViewStateKey"
    }
}