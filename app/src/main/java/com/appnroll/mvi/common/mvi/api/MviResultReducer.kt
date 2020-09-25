package com.appnroll.mvi.common.mvi.api

interface MviResultReducer<R : MviResult, VS : MviViewState> {

    fun default(): VS

    fun VS.reduce(result: R): VS

    fun fold(current: VS): VS? = current
}