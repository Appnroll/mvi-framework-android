package com.appnroll.mvi.common.mvi.viewstate

import androidx.lifecycle.SavedStateHandle

interface MviViewStateCache<VS : MviViewState> {

    fun get(): VS?

    fun set(viewState: VS)
}

open class MviViewStateCacheImpl<VS : MviViewState>(
    private val key: String,
    private val savedStateHandle: SavedStateHandle
) : MviViewStateCache<VS> {

    override fun get() = savedStateHandle.get<VS>(key)

    override fun set(viewState: VS) = savedStateHandle.set(key, viewState)
}