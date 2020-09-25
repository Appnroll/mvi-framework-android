package com.appnroll.mvi.common.mvi.model

import kotlinx.coroutines.flow.Flow

interface MviActionProcessor<A, R> : (A) -> Flow<R>