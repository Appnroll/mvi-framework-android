package com.appnroll.mvi.common.mvi.api

import kotlinx.coroutines.flow.Flow

/**
 * Describes how one action object instance is transforming into flow of Results.
 *
 * By convention - each MviAction type should have its separate MviActionProcessor
 */
interface MviActionProcessor<A : MviAction, R : MviResult> : (A) -> Flow<R>