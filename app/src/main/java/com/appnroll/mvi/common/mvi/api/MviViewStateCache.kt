package com.appnroll.mvi.common.mvi.api

interface MviViewStateCache<VS : MviViewState> {

    fun get(): VS?

    fun set(viewState: VS)
}

