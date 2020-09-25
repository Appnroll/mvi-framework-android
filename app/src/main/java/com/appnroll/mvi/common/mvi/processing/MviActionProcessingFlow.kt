package com.appnroll.mvi.common.mvi.processing

import com.appnroll.mvi.common.mvi.api.MviActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.impl.HomeAction
import com.appnroll.mvi.ui.components.home.mvi.impl.HomeResult
import com.appnroll.mvi.ui.components.home.mvi.impl.HomeResultReducer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

/*
* Logic Controller
* */
open class MviActionProcessingFlow<A : Any, R> private constructor(
    mviActionProcessor: MviActionProcessor<A, R>,
    private val actionChannel: Channel<A>,
    private val resultFlow: Flow<R> = actionChannel.receiveAsFlow().asProcessingFlow(mviActionProcessor)
) : Flow<R> by resultFlow {

    /**
     * This constructor was added because `resultFlow` field needs to be initialized in constructor
     * (because it is used in the delegation) but it should not be possible to override this field
     * when creating class object - that is why primary constructor is private.
     */
    constructor(
        mviActionProcessor: MviActionProcessor<A, R>
    ) : this(
        mviActionProcessor = mviActionProcessor,
        actionChannel = Channel(Channel.UNLIMITED)
    )

    suspend fun accept(action: A) {
        actionChannel.send(action)
    }
}

/**
 * Parallel Flow Builder
 * add description
 * */
private fun <A : Any, R> Flow<A>.asProcessingFlow(
    shouldRestart: Boolean = true,
    producer: (A) -> Flow<R>
): Flow<R> {
    val internalJobs: HashMap<Any, Job> = hashMapOf()
    @Suppress("EXPERIMENTAL_API_USAGE")
    return channelFlow {
        collect { action: A ->
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
    }
}

private inline fun <A : Any, R> Flow<A>.asProcessingFlow(
    crossinline producer: (A) -> Flow<R>
): Flow<R> = asProcessingFlow(shouldRestart = true) { producer(it) }