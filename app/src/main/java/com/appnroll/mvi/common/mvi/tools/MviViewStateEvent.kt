package com.appnroll.mvi.common.mvi.tools

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.concurrent.atomic.AtomicBoolean

@Parcelize
data class ViewStateEvent<T : Parcelable>(
    val payload: T,
    override val isConsumed: AtomicBoolean = AtomicBoolean(false)
) : SingleEvent<T>(payload), Parcelable {

    override fun equals(other: Any?) = super.equals(other)
    override fun hashCode() = 31 * payload.hashCode() + isConsumed.hashCode()
}

/**
 * ViewState event generic class for a simple types like String, Int, Float, Double etc.
 */
@Parcelize
data class ViewStateSimpleTypeEvent<T : Any>(
    val payload: @RawValue T,
    override val isConsumed: AtomicBoolean = AtomicBoolean(false)
) : SingleEvent<T>(payload), Parcelable {

    override fun equals(other: Any?) = super.equals(other)
    override fun hashCode() = 31 * payload.hashCode() + isConsumed.hashCode()
}

data class ViewStateNonParcelableEvent<T>(
    val payload: T
) : SingleEvent<T>(payload) {

    override fun equals(other: Any?) = super.equals(other)
    override fun hashCode() = 31 * payload.hashCode() + isConsumed.hashCode()
}

@Parcelize
data class ViewStateErrorEvent(
    val payload: Throwable,
    override val isConsumed: AtomicBoolean = AtomicBoolean(false)
) : SingleEvent<Throwable>(payload), Parcelable {

    override fun equals(other: Any?) = super.equals(other)
    override fun hashCode() = 31 * payload.hashCode() + isConsumed.hashCode()
}

@Parcelize
class ViewStateEmptyEvent(
    override val isConsumed: AtomicBoolean = AtomicBoolean(false)
) : SingleEvent<Unit>(Unit), Parcelable {

    override fun equals(other: Any?) = super.equals(other)
    override fun hashCode() = isConsumed.hashCode()
}

abstract class SingleEvent<T>(
    private val argument: T,
    protected open val isConsumed: AtomicBoolean = AtomicBoolean(false)
) {

    private fun isConsumed(setAsConsumed: Boolean = false) = isConsumed.getAndSet(setAsConsumed)

    fun consume(action: (T) -> Unit) {
        if (!isConsumed.getAndSet(true)) {
            action.invoke(argument)
        }
    }

    fun resend() = isConsumed.set(false)
}