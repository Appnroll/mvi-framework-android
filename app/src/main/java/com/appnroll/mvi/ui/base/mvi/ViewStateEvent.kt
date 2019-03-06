package com.appnroll.mvi.ui.base.mvi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.concurrent.atomic.AtomicBoolean


@Parcelize
data class ViewStateEvent<T: Serializable>(
    val payload: T? = null,
    val isConsumed: AtomicBoolean = AtomicBoolean(false)
): Parcelable {

    private fun isConsumed(setAsConsumed: Boolean = false) = isConsumed.getAndSet(setAsConsumed)

    fun handle(action: (T?) -> Unit) {
        if (!isConsumed()) {
            action(payload)
        }
    }

    fun consume(action: (T?) -> Unit) {
        if (!isConsumed(true)) {
            action(payload)
        }
    }
}

typealias ViewStateEmptyEvent = ViewStateEvent<Serializable>