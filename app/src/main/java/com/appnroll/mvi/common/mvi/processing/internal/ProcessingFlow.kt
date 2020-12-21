package com.appnroll.mvi.common.mvi.processing.internal

import com.appnroll.mvi.common.mvi.api.MviActionProcessor
import com.appnroll.mvi.common.mvi.api.MviViewState
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
internal class ProcessingFlow<VS : MviViewState>(
    channel: ReceiveChannel<MviActionProcessor<VS>>,
    shouldRestart: Boolean = true // currently not used - always true
) : Flow<VS.() -> VS> by channelFlow(
    block = {
        val internalJobs: HashMap<Any, Job> = hashMapOf()

        channel.receiveAsFlow().collect { processor: MviActionProcessor<VS> ->
            val currentJob = internalJobs[processor.getId()]
            val shouldStart =
                shouldRestart || currentJob == null || currentJob.isCompleted
            if (shouldRestart) {
                currentJob?.cancel()
                println("cancelling job for action: $processor, isCompleted: ${currentJob?.isCompleted}, job: $currentJob")
            }
            if (shouldStart) {
                val newJob = launch(Dispatchers.Default) { processor().collect(::send) }
                println("starting job for action: $processor, job: $newJob")
                internalJobs[processor.getId()] = newJob
            }
        }
    })