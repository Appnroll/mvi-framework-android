package com.appnroll.mvi.common.mvi.internal

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Parallel Flow Builder
 * add description
 * */
fun <T, R> Flow<T>.asProcessingFlow(
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

inline fun <T, R> Flow<T>.asProcessingFlow(
    crossinline producer: (T) -> Flow<R>
): Flow<R> = asProcessingFlow(shouldRestart = true) { producer(it) }