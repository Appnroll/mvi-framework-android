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
abstract class MviActionProcessingFlow<A, R> : Flow<R> {

    private val actionChannel = Channel<A>(Channel.UNLIMITED)
    private val resultFlow = actionChannel
        .receiveAsFlow()
        .asProcessingFlow(::getProcessor)

    abstract fun getProcessor(action: A): Flow<R>

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
typealias MviProcessor<A, R> = (action: A) -> Flow<R>