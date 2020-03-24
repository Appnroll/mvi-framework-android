package com.appnroll.mvi.utils

import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent

inline fun LifecycleOwner.whenStarted(crossinline body: () -> Unit) {
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(ON_START)
        fun started() = body()
    })
}

inline fun LifecycleOwner.whenStopped(crossinline body: () -> Unit) {
    lifecycle.addObserver(object : LifecycleObserver {
        @OnLifecycleEvent(ON_STOP)
        fun stopped() = body()
    })
}