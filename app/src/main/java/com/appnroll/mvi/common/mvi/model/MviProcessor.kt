package com.appnroll.mvi.common.mvi.model

import kotlinx.coroutines.flow.Flow

/*
* Logic Processor
* */
typealias MviProcessor<A, R> = (A) -> Flow<R>

@Suppress("NOTHING_TO_INLINE")
inline fun <A : MviAction, R : MviResult> mviProcessor(
    noinline builder: (A) -> Flow<R>
): MviProcessor<A, R> = builder
