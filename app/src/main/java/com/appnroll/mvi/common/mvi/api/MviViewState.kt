package com.appnroll.mvi.common.mvi.api

import android.os.Parcelable

/**
 * Representation of an UI state
 */
interface MviViewState : Parcelable {
    val isSavable: Boolean
}