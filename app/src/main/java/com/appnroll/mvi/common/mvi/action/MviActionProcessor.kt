package com.appnroll.mvi.common.mvi.action

import kotlinx.coroutines.flow.Flow

interface MviActionProcessor<A, R> : (A) -> Flow<R>