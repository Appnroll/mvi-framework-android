package com.appnroll.mvi.ui.base.mvi

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.concurrent.atomic.AtomicBoolean


@Parcelize
data class ViewStateEvent<T: Parcelable>(
    val payload: T,
    override val isConsumed: AtomicBoolean = AtomicBoolean(false)
): SingleEvent<T>(payload), Parcelable {
    
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
    
    override fun hashCode(): Int {
        var result = payload.hashCode()
        result = 31 * result + isConsumed.hashCode()
        return result
    }
}


data class ViewStateNonParcelableEvent<T>(
    val payload: T
): SingleEvent<T>(payload){
    
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
    
    override fun hashCode(): Int {
        var result = payload.hashCode()
        result = 31 * result + isConsumed.hashCode()
        return result
    }
}


@Parcelize
data class ViewStateErrorEvent(
    val payload: Throwable,
    override val isConsumed: AtomicBoolean = AtomicBoolean(false)
): SingleEvent<Throwable>(payload), Parcelable {
    
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
    
    override fun hashCode(): Int {
        var result = payload.hashCode()
        result = 31 * result + isConsumed.hashCode()
        return result
    }
}


@Parcelize
class ViewStateEmptyEvent(
    override val isConsumed: AtomicBoolean = AtomicBoolean(false)
): SingleEvent<Unit>(Unit), Parcelable {
    
    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }
    
    override fun hashCode(): Int {
        return isConsumed.hashCode()
    }
}


abstract class SingleEvent<T>(
    private val argument: T,
    protected open val isConsumed: AtomicBoolean = AtomicBoolean(false)
) {
    
    private fun isConsumed(setAsConsumed: Boolean = false) = isConsumed.getAndSet(setAsConsumed)
    
    fun consume(action: (T) -> Unit) {
        if (!isConsumed(true)) {
            action.invoke(argument)
        }
    }
}