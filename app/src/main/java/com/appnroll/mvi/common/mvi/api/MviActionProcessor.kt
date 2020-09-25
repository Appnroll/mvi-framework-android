package com.appnroll.mvi.common.mvi.api

import kotlinx.coroutines.flow.Flow

interface MviActionProcessor<A, R> : (A) -> Flow<R>