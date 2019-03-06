package com.appnroll.mvi.ui.base.mvi

import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.CompositeDisposable


abstract class MviViewModel<A: MviAction, R: MviResult, VS: MviViewState<R>>(
    private val actionProcessor: ObservableTransformer<A, R>,
    private val defaultViewState: VS
): ViewModel() {

    private var disposables: CompositeDisposable = CompositeDisposable()

    private val actionSource = PublishRelay.create<A>()

    private var viewStatesObservable: Observable<VS>? = null

    @Suppress("UNCHECKED_CAST")
    fun initViewStatesObservable(savedViewState: VS?) {
        if (viewStatesObservable == null) {
            viewStatesObservable = actionSource
                .compose(actionProcessor)
                .scan<VS>(savedViewState ?: defaultViewState) { viewState: VS, result -> viewState.reduce(result) as VS }
                .distinctUntilChanged()
                .replay(1)
                .autoConnect(0)
        }
    }

    fun getViewStatesObservable() =
        viewStatesObservable ?: throw Exception(
            "You need to invoke initViewStatesObservable(lastViewState, alwaysInitWithSavedViewState) " +
                    "in onCreate() method of Your Activity or onViewCreated() method of Your Fragment"
        )

    fun processActions(actionsObservable: Observable<A>) {
        actionsObservable.subscribe(actionSource).run { disposables.add(this) }
    }

    fun clear() {
        disposables.clear()
    }
}