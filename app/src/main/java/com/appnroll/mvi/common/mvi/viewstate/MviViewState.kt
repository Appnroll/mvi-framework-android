package com.appnroll.mvi.common.mvi.viewstate

import android.os.Parcelable

interface MviViewState : Parcelable {
    val isSavable: Boolean
}