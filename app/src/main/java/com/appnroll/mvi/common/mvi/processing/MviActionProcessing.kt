package com.appnroll.mvi.common.mvi.processing

import com.appnroll.mvi.common.mvi.api.MviAction
import com.appnroll.mvi.common.mvi.api.MviActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.impl.HomeAction
import com.appnroll.mvi.ui.components.home.mvi.impl.HomeResult
import com.appnroll.mvi.ui.components.home.mvi.impl.HomeResultReducer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

/*
* Logic Controller
* */
open class MviActionProcessing<A : MviAction, R>(
    mviActionProcessor: MviActionProcessor<A, R>
) {
    private val input: Channel<A> = Channel(Channel.UNLIMITED)
    private val output: Flow<R> = ProcessingFlow(input, mviActionProcessor)

    val resultsFlow: Flow<R> = output

    suspend fun accept(action: A) = input.send(action)
}

/**
 * Parallel Flow Builder
 * add description
 * */
@Suppress("EXPERIMENTAL_API_USAGE")
class ProcessingFlow<A : MviAction, R>(
    channel: ReceiveChannel<A>,
    producer: (A) -> Flow<R>,
    shouldRestart: Boolean = true
) : Flow<R> by channelFlow(
    block = {
        val internalJobs: HashMap<Any, Job> = hashMapOf()
        channel.receiveAsFlow().collect { action: A ->
            /*
             TODO:
              Currently only one action of the current type could be processed at once.
              Think how we could process more then one action of the same type at once,
              but in a way that we will have full control of the uniqueness af an action
             */
            val currentJob = internalJobs[action::class]
            val shouldStart =
                shouldRestart || currentJob == null || currentJob.isCompleted
            if (shouldRestart) {
                currentJob?.cancel()
                println("cancelling job for action: $action, isCompleted: ${currentJob?.isCompleted}, job: $currentJob")
            }
            if (shouldStart) {
                val newJob = launch(Dispatchers.Default) { producer(action).collect(::send) }
                println("starting job for action: $action, job: $newJob")
                internalJobs[action::class] = newJob
            }
        }
    })