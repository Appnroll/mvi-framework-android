package com.appnroll.mvi.common.mvi.api

import android.os.Parcelable

interface MviViewState : Parcelable {
    val isSavable: Boolean
}