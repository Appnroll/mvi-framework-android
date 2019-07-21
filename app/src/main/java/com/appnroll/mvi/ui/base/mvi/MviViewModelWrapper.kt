package com.appnroll.mvi.ui.base.mvi

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo


class MviViewModelWrapper<A: MviAction, R: MviResult, VS: MviViewState<R>> private constructor(
    viewModelProvider: ViewModelProvider,
    viewModelClass: Class<out MviViewModel<A, R, VS>>,
    lifecycle: Lifecycle
) {
    
    constructor(fragment: Fragment, viewModelClass: Class<out MviViewModel<A, R, VS>>): this(
        ViewModelProviders.of(fragment, SavedStateViewModelFactory(fragment)),
        viewModelClass,
        fragment.lifecycle
    )
    
    constructor(activity: AppCompatActivity, viewModelClass: Class<out MviViewModel<A, R, VS>>): this(
        ViewModelProviders.of(activity, SavedStateViewModelFactory(activity)),
        viewModelClass,
        activity.lifecycle
    )
    
    val viewModel: MviViewModel<A, R, VS> by lazy {
        viewModelProvider.get(viewModelClass).apply {
            onLifecycleAttached(lifecycle)
        }
    }
    
    private var disposables = CompositeDisposable()
    
    fun startProcessing(render: (VS) -> Unit) {
        viewModel.viewStatesObservable.subscribe(render).addTo(disposables)
        viewModel.startProcessingActions()
    }
    
    fun stopProcessing() {
        disposables.clear()
        viewModel.stopProcessingActions()
    }
}