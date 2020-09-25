package com.appnroll.mvi.common.mvi.utils

import androidx.lifecycle.SavedStateHandle
import com.appnroll.mvi.common.mvi.api.MviViewState

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