package com.appnroll.mvi.common.mvi

import androidx.lifecycle.SavedStateHandle
import com.appnroll.mvi.common.mvi.state.MviViewState

interface MviViewStateCache<VS: MviViewState> {

    fun get(): VS?

    fun set(viewState: VS)
}

class MviViewStateCacheImpl<VS: MviViewState>(
    private val key: String,
    private val savedStateHandle: SavedStateHandle
): MviViewStateCache<VS> {

    override fun get() = savedStateHandle.get<VS>(key)

    override fun set(viewState: VS) = savedStateHandle.set(key, viewState)
}