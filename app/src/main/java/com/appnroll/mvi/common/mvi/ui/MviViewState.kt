package com.appnroll.mvi.common.mvi.ui

import android.os.Parcelable

interface MviViewState : Parcelable {
    val isSavable: Boolean
}