package com.appnroll.mvi.common.mvi.state

import android.os.Parcelable

interface MviViewState : Parcelable {
    val isSavable: Boolean
}