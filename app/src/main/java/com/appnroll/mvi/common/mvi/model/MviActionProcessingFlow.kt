package com.appnroll.mvi.common.mvi.model

import com.appnroll.mvi.common.mvi.internal.asProcessingFlow
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.receiveAsFlow

/*
* Logic Controller
* */
open class MviActionProcessingFlow<A, R>(
    mviActionProcessor: MviActionProcessor<A, R>
) : Flow<R> /* TODO: add "by resultFlow" */ {

    private val actionChannel = Channel<A>(Channel.UNLIMITED)
    private val resultFlow = actionChannel
        .receiveAsFlow()
        .asProcessingFlow(mviActionProcessor)

    suspend fun accept(action: A) {
        actionChannel.send(action)
    }

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<R>) {
        resultFlow.collect(collector)
    }
}

/*
* Logic Processor
* */
interface MviActionProcessor<A, R>: (A) -> Flow<R>