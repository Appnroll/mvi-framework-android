package com.appnroll.mvi.ui.base.mvi

import com.appnroll.mvi.common.SchedulersProvider
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer

abstract class MviActionsProcessor<A : MviAction, R : MviResult> : ObservableTransformer<A, R> {

    final override fun apply(actions: Observable<A>): ObservableSource<R> {
        return actions.publish { shared ->
            Observable.merge(
                getActionProcessors(shared)
            )
        }
    }

    abstract fun getActionProcessors(shared: Observable<A>): List<Observable<R>>

    inline fun <reified A> Observable<in A>.connect(processor: ObservableTransformer<A, R>): Observable<R> {
        return ofType(A::class.java).compose(processor)
    }
}

fun <A : MviAction, R : MviResult> createActionProcessor(
    schedulersProvider: SchedulersProvider? = null,
    initialResult: ((a: A) -> R?)? = null,
    onErrorResult: ((t: Throwable) -> R)? = null,
    doStuff: ObservableEmitter<R>.(action: A) -> Unit
) = ObservableTransformer<A, R> { actions ->
    actions
        .switchMap { action ->
            var observable = asObservable<R> {
                doStuff(action)
            }
            schedulersProvider?.let {
                observable = observable
                    .subscribeOn(schedulersProvider.subscriptionScheduler())
                    .observeOn(schedulersProvider.observationScheduler())
            }

            if (onErrorResult != null) {
                observable = observable.onErrorReturn { t -> onErrorResult.invoke(t) }
            }

            if (initialResult != null) {
                observable = observable.startWith(initialResult.invoke(action))
            }
            observable
        }
}

fun <T> asObservable(doStuff: ObservableEmitter<T>.() -> Unit): Observable<T> =
    Observable.create<T> { emitter ->
        emitter.doStuff()
        emitter.onCompleteSafe()
    }

fun <T> ObservableEmitter<T>.onNextSafe(item: T) {
    if (!isDisposed) {
        onNext(item)
    }
}

fun <T> ObservableEmitter<T>.onCompleteSafe() {
    if (!isDisposed) {
        onComplete()
    }
}