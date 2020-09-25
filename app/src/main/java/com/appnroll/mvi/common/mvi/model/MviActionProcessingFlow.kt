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
    mviActionProcessor: MviActionProcessor<A, R>,
    private val actionChannel: Channel<A> = Channel(Channel.UNLIMITED),
    private val resultFlow: Flow<R> = actionChannel.receiveAsFlow().asProcessingFlow(mviActionProcessor)
) : Flow<R> by resultFlow {

    suspend fun accept(action: A) {
        actionChannel.send(action)
    }
}