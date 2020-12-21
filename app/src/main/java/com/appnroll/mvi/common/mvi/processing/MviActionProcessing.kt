package com.appnroll.mvi.common.mvi.processing

import com.appnroll.mvi.common.mvi.api.MviActionProcessor
import com.appnroll.mvi.common.mvi.api.MviViewState
import com.appnroll.mvi.common.mvi.processing.internal.ProcessingFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow

/**
 * Wrapper around MviActionProcessors.
 *
 * It consumes MviAction's {fun accept(...)} then passes it to MviActionProcessing which produces
 * the flow of MviResult's which is available outside with a property `val resultsFlow`
 *
 */
open class MviActionProcessing<VS : MviViewState>(
) {
    private val input: Channel<MviActionProcessor<VS>> = Channel(Channel.UNLIMITED)
    private val output: Flow<VS.() -> VS> = ProcessingFlow(
        channel = input
    )

    val resultsFlow: Flow<VS.() -> VS> = output

    suspend fun accept(processor: MviActionProcessor<VS>) = input.send(processor)
}
