package com.appnroll.mvi.common.mvi.api

import kotlinx.coroutines.flow.Flow

/**
 * Describes how one MviAction object instance is transforming into flow of MviResult's.
 *
 * By convention - each MviAction type should have its own MviActionProcessor.
 */
interface MviActionProcessor<A : MviAction, R : MviResult> : (A) -> Flow<R>