package com.appnroll.mvi.ui.base.mvi

import android.os.Parcelable


interface MviAction

interface MviResult

interface MviViewState<R: MviResult>: Parcelable {

    fun reduce(result: R): MviViewState<R>

    fun isSavable(): Boolean
}
