package com.appnroll.mvi.ui.base.mvi

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.disposables.CompositeDisposable


class MviController<A: MviAction, R: MviResult, VS: MviViewState<R>>(
    private val viewModelProvider: ViewModelProvider,
    private val viewStateParcelKey: String,
    private val lifecycle: Lifecycle,
    private val render: (VS) -> Unit,
    private val initialAction: ((VS?) -> A?)
): LifecycleObserver {

    var viewState: VS? = null

    private lateinit var viewModel: MviViewModel<A, R, VS>

    private val actionsRelay = PublishRelay.create<A>()
    private var disposable = CompositeDisposable()

    fun initViewModel(viewModelClass: Class<out MviViewModel<A, R, VS>>) {
        viewModel = viewModelProvider.get(viewModelClass)
        lifecycle.addObserver(this)
    }

    fun initViewStatesObservable(savedInstanceState: Bundle?) {
        val lastViewState = savedInstanceState?.getParcelable(viewStateParcelKey) as? VS? ?: viewState
        viewModel.initViewStatesObservable(lastViewState)
    }

    fun accept(action: A) {
        actionsRelay.accept(action)
    }

    fun saveLastViewState(outState: Bundle) {
        outState.putParcelable(viewStateParcelKey, viewState)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        viewModel.getViewStatesObservable().subscribe(this::render).run { disposable.add(this) }
        viewModel.processActions(actionsRelay)

        initialAction(viewState)?.let { action -> accept(action) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        disposable.clear()
        viewModel.clear()
    }

    private fun render(viewState: VS) {
        render.invoke(viewState)
        if (viewState.isSavable()) {
            this.viewState = viewState
        }
    }
}