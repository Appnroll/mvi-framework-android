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
        VS : MviViewState>(
    private val savedStateHandle: SavedStateHandle,
    mviFlow: mviFlow
) : ViewModel(), MviStateController<A, VS> {

    @PublishedApi
    internal var viewState: VS =
        savedStateHandle.get<VS>(VIEW_STATE_KEY) ?: reducer.default()

    private val actionsObserver = PublishRelay.create<A>()
    private val actionsSource = PublishRelay.create<A>()
    private val disposable = actionsSource.subscribe(actionsObserver)

    /*
    * If we would separate common/business logic flow Action -> AP -> Result then this part could be
    * delegate to staged logic A->AP->R then second loop R->Reducer->VS. This is already partially
    * implemented by action processors.
    *
    * Extended ActionProcessor implements relays, subject configuration and cleanup
    * New StateProcessor implements results transformation into States, observable configuration and cleanup
    * New StateProcessor subscribes to Action Processor outputs
    * New StateProcessor contains Result Reducer
    *
    * New StateController class connects ActionsProcessor and StateProcessor uniquely.
    * From the outside New StateController declares only Actions and ViewStates, not ActionsProcessors, StateProcessors and Reducers
    * */
    private val viewStatesObservable: Observable<VS> by lazy {
        actionsObserver
            .compose(actionProcessor)
            .distinctUntilChanged()
            .replay(1)
            .autoConnect(0)




            .scan<VS>(viewState, reducer::reduce)
            /*this could be decompose into few observers, each one doing exactly on job (scan, savestate, observe)*/
            .doOnNext { state ->
                if (state.isSavable) {
                    savedStateHandle.set(VIEW_STATE_KEY, reducer::fold)
                }
                viewState = state
            }

    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
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


    companion object {
        private const val VIEW_STATE_KEY = "ViewStateKey"
    }
}
