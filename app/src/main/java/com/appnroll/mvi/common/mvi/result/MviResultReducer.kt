package com.appnroll.mvi.common.mvi.result

import com.appnroll.mvi.common.mvi.viewstate.MviViewState

interface MviResultReducer<R : MviResult, VS : MviViewState> {

    fun default(): VS

    fun VS.reduce(result: R): VS

    fun fold(current: VS): VS? = current
}