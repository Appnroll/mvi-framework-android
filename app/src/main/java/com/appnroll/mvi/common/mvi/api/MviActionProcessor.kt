package com.appnroll.mvi.common.mvi.api

import kotlinx.coroutines.flow.Flow

interface MviActionProcessor<A : MviAction, R : MviResult> : (A) -> Flow<R>