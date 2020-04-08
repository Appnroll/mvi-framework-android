package com.appnroll.mvi.ui.base.mvi

import android.os.Parcelable

interface MviAction

interface MviResult

interface MviViewState : Parcelable {
    val isSavable: Boolean
}

interface MviReducer<R : MviResult, S : MviViewState> {
    fun default(): S
    fun reduce(current: S, result: R): S
    fun fold(current: S): S? = current
}