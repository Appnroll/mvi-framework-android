package com.appnroll.mvi.ui.base.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.disposables.Disposable

@Suppress("UNCHECKED_CAST")
class MviViewModel<
        A : MviAction,
        R : MviResult,
        VS : MviViewState>(
    private val savedStateHandle: SavedStateHandle,
    private val reducer: MviResultReducer<R, VS>,
    actionProcessor: ObservableTransformer<A, R>
) : ViewModel(), MviStateProcessor<A, VS> {

    @PublishedApi
    internal var viewState: VS =
        savedStateHandle.get<VS>(VIEW_STATE_KEY) ?: reducer.default()

    private val actionsObserver = PublishRelay.create<A>()
    private val actionsSource = PublishRelay.create<A>()
    private val disposable = actionsSource.subscribe(actionsObserver)

    private val viewStatesObservable: Observable<VS> by lazy {
        actionsObserver
            .compose(actionProcessor)
            .scan<VS>(viewState, reducer::reduce)
            .distinctUntilChanged()
            .doOnNext { state ->
                if (state.isSavable) {
                    savedStateHandle.set(VIEW_STATE_KEY, reducer::fold)
                    viewState = state
                }
            }
            .replay(1)
            .autoConnect(0)
    }

    override fun init(render: (VS) -> Unit): Disposable {
        return viewStatesObservable.subscribe(render)
    }

    override fun accept(intent: VS.() -> A?) {
        accept(
            action = viewState.intent() ?: return
        )
    }

    fun accept(action: A) {
        actionsSource.accept(action)
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    companion object {
        private const val VIEW_STATE_KEY = "ViewStateKey"
    }
}