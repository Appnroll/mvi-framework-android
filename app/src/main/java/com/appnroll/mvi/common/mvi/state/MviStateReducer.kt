package com.appnroll.mvi.common.mvi.state

import com.appnroll.mvi.common.mvi.model.MviResult

interface MviStateReducer<R : MviResult, VS : MviViewState> {

    fun default(): VS

    fun VS.reduce(result: R): VS

    fun fold(current: VS): VS? = current
}