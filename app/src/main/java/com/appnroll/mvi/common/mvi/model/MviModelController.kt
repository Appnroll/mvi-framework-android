package com.appnroll.mvi.common.mvi.model

import com.appnroll.mvi.common.mvi.MviController
import com.appnroll.mvi.common.mvi.internal.asProcessingFlow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.reflect.KClass

fun <A : MviAction, R : MviResult> modelControllerOf(
    @Suppress("UNUSED_PARAMETER") actionFamily: KClass<A>,
    processor: MviProcessor<A, R>
): MviController<A, R> {
    val actionChannel = Channel<A>(Channel.UNLIMITED)
    val resultFlow = actionChannel
        .receiveAsFlow()
        .asProcessingFlow(processor)

    return object : MviController<A, R>, Flow<R> by resultFlow {
        override suspend fun accept(value: A) {
            actionChannel.send(value)
        }
    }
}

inline fun <reified A : MviAction, R : MviResult> modelControllerOf(
    noinline processor: MviProcessor<A, R>
): MviController<A, R> = modelControllerOf(A::class, processor)