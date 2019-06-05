package com.appnroll.mvi.ui.base.mvi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.concurrent.atomic.AtomicBoolean


@Parcelize
data class ViewStateEvent<T: Parcelable>(
    val payload: T
): SingleEvent<T>({ payload })


@Parcelize
data class ViewStateErrorEvent(
    val payload: Throwable
): SingleEvent<Throwable>({ payload })


@Parcelize
class ViewStateEmptyEvent: SingleEvent<Unit>({ Unit })


abstract class SingleEvent<T>(
    val argument: () -> T,
    private val isConsumed: AtomicBoolean = AtomicBoolean(false)
): Parcelable {

    private fun isConsumed(setAsConsumed: Boolean = false) = isConsumed.getAndSet(setAsConsumed)

    fun consume(action: (T) -> Unit) {
        if (!isConsumed(true)) {
            action.invoke(argument())
        }
    }
}