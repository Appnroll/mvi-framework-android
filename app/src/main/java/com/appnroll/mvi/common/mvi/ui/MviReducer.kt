package com.appnroll.mvi.common.mvi.ui

import com.appnroll.mvi.common.mvi.model.MviResult

interface MviReducer<R : MviResult, S : MviViewState> {
    fun default(): S
    fun S.reduce(result: R): S
    fun fold(current: S): S? = current
}