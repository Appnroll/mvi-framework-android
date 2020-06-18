package com.appnroll.mvi.common.mvi

import kotlinx.coroutines.flow.Flow

/*
* Logic Controller
* */
interface MviController<A, R> : Flow<R> {
    suspend fun accept(value: A)
}