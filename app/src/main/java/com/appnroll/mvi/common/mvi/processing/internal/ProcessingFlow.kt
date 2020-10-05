package com.appnroll.mvi.common.mvi.processing.internal

import com.appnroll.mvi.common.mvi.api.MviAction
import com.appnroll.mvi.common.mvi.api.MviActionProcessor
import com.appnroll.mvi.common.mvi.api.MviResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Parallel Flow Builder
 * add description
 * */
@Suppress("EXPERIMENTAL_API_USAGE")
internal class ProcessingFlow<A : MviAction, R : MviResult>(
    channel: ReceiveChannel<A>,
    processor: MviActionProcessor<A, R>,
    shouldRestart: Boolean = true // currently not used - always true
) : Flow<R> by channelFlow(
    block = {
        val internalJobs: HashMap<Any, Job> = hashMapOf()

        channel.receiveAsFlow().collect { action: A ->
            val currentJob = internalJobs[action.getId()]
            val shouldStart =
                shouldRestart || currentJob == null || currentJob.isCompleted
            if (shouldRestart) {
                currentJob?.cancel()
                println("cancelling job for action: $action, isCompleted: ${currentJob?.isCompleted}, job: $currentJob")
            }
            if (shouldStart) {
                val newJob = launch(Dispatchers.Default) { processor(action).collect(::send) }
                println("starting job for action: $action, job: $newJob")
                internalJobs[action.getId()] = newJob
            }
        }
    })