package com.appnroll.mvi.common.mvi.action

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

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

/**
* Parallel Flow Builder
* add description
* */
private fun <T, R> Flow<T>.asProcessingFlow(
    shouldRestart: Boolean = true,
    producer: (T) -> Flow<R>
): Flow<R> {
    val internalJobs: HashMap<T, Job> = hashMapOf()
    @Suppress("EXPERIMENTAL_API_USAGE")
    return channelFlow<R> {
        collect { action: T ->
            val currentJob = internalJobs[action]
            val shouldStart =
                shouldRestart || currentJob == null || currentJob.isCompleted

            if (shouldRestart) {
                internalJobs[action]?.cancel()
                println("cancelling new $action")
            }

            if (shouldStart) {
                println("starting new $action")
                internalJobs[action] =
                    launch(Dispatchers.Default) { producer(action).collect(::send) }
            }
        }
    }
}

private inline fun <T, R> Flow<T>.asProcessingFlow(
    crossinline producer: (T) -> Flow<R>
): Flow<R> = asProcessingFlow(shouldRestart = true) { producer(it) }