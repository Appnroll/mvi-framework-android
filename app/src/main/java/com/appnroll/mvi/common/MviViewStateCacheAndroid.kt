package com.appnroll.mvi.common

import androidx.lifecycle.SavedStateHandle
import com.appnroll.mvi.common.mvi.api.MviViewState
import com.appnroll.mvi.common.mvi.api.MviViewStateCache

open class MviViewStateCacheAndroid<VS : MviViewState>(
    private val key: String,
    private val savedStateHandle: SavedStateHandle
) : MviViewStateCache<VS> {

    override fun get() = savedStateHandle.get<VS>(key)

    override fun set(viewState: VS) = savedStateHandle.set(key, viewState)
}