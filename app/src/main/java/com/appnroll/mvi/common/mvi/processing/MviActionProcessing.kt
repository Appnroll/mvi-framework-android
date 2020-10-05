package com.appnroll.mvi.common.mvi.processing

import com.appnroll.mvi.common.mvi.api.MviAction
import com.appnroll.mvi.common.mvi.api.MviActionProcessor
import com.appnroll.mvi.common.mvi.api.MviResult
import com.appnroll.mvi.common.mvi.processing.internal.ProcessingFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

/**
 * Wrapper around MviActionProcessors.
 *
 * It consumes MviAction's {fun accept(...)} then passes it to MviActionProcessing which produces
 * the flow of MviResult's which is available outside with a property `val resultsFlow`
 *
 * @param mviActionProcessor object responsible for transforming MviAction into flow of MviResults
 */
open class MviActionProcessing<A : MviAction, R : MviResult>(
    mviActionProcessor: MviActionProcessor<A, R>
) {
    private val input: Channel<A> = Channel(Channel.UNLIMITED)
    private val output: Flow<R> = ProcessingFlow(
        channel = input,
        processor = mviActionProcessor
    )

    val resultsFlow: Flow<R> = output

    suspend fun accept(action: A) = input.send(action)
}
