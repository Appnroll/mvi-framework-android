package com.appnroll.mvi.ui.base.mvi

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import io.reactivex.disposables.CompositeDisposable


class MviController<A: MviAction, R: MviResult, VS: MviViewState<R>>(
    private val viewModelProvider: ViewModelProvider,
    private val lifecycle: Lifecycle,
    private val callback: MviControllerCallback<A, R, VS>
): LifecycleObserver {

    lateinit var viewModel: MviViewModel<A, R, VS>

    private var disposable = CompositeDisposable()

    fun initViewModel(viewModelClass: Class<out MviViewModel<A, R, VS>>) {
        viewModel = viewModelProvider.get(viewModelClass)
        viewModel.onLifecycleAttached(lifecycle)
        lifecycle.addObserver(this)
    }

    fun initViewStatesObservable() {
        viewModel.initViewStatesObservable()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun onStart() {
        viewModel.getViewStatesObservable().subscribe(this::render).run { disposable.add(this) }
        viewModel.startProcessingActions()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun onStop() {
        disposable.clear()
        viewModel.stopProcessingActions()
    }

    private fun render(viewState: VS) {
        callback.render(viewState)
    }
}

interface MviControllerCallback<A: MviAction, R: MviResult, VS: MviViewState<R>> {

    /**
     * Update UI based on ViewState
     */
    fun render(viewState: VS)
}