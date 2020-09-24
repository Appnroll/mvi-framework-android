package com.appnroll.mvi.common.mvi

import com.appnroll.mvi.common.mvi.internal.asProcessingFlow
import com.appnroll.mvi.common.mvi.model.MviAction
import com.appnroll.mvi.common.mvi.model.MviProcessor
import com.appnroll.mvi.common.mvi.model.MviResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.reflect.KClass

@Deprecated("user MviActionProcessingFlow")
interface MviControllerOld<A, R> : Flow<R> {
    suspend fun accept(action: A)
}

@Deprecated("use MviActionProcessingFlow")
fun <A : MviAction, R : MviResult> modelControllerOf(
    @Suppress("UNUSED_PARAMETER") actionFamily: KClass<A>,
    processor: MviProcessor<A, R>
): MviControllerOld<A, R> {
    val actionChannel = Channel<A>(Channel.UNLIMITED)
    val resultFlow: Flow<R> = actionChannel
        .receiveAsFlow()
        .asProcessingFlow(processor)

    return object : MviControllerOld<A, R>, Flow<R> by resultFlow {
        override suspend fun accept(value: A) {
            actionChannel.send(value)
        }
    }
}

@Deprecated("use MviActionProcessingFlow")
inline fun <reified A : MviAction, R : MviResult> modelControllerOf(
    noinline processor: MviProcessor<A, R>
): MviControllerOld<A, R> = modelControllerOf(A::class, processor)