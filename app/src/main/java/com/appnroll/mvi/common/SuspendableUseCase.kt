package com.appnroll.mvi.common


/**
 * Interface is needed as we are not able to use suspend function as a parent
 * interface SampleUseCase: suspend () -> Unit
 */

interface SuspendableUseCase0<R> {

    suspend operator fun invoke(): R
}

interface SuspendableUseCase1<A1, R> {

    suspend operator fun invoke(a1: A1): R
}

interface SuspendableUseCase2<A1, A2, R> {

    suspend operator fun invoke(a1: A1, a2: A2): R
}

interface SuspendableUseCase3<A1, A2, A3, R> {

    suspend operator fun invoke(a1: A1, a2: A2, a3: A3): R
}

interface SuspendableUseCase4<A1, A2, A3, A4, R> {

    suspend operator fun invoke(a1: A1, a2: A2, a3: A3, a4: A4): R
}

interface SuspendableUseCase5<A1, A2, A3, A4, A5, R> {

    suspend operator fun invoke(a1: A1, a2: A2, a3: A3, a4: A4, a5: A5): R
}

/**
 * Suspendable use cases without returning result type
 */

interface SuspendableUseCase0n {

    suspend operator fun invoke()
}

interface SuspendableUseCase1n<A1> {

    suspend operator fun invoke(a1: A1)
}

interface SuspendableUseCase2n<A1, A2> {

    suspend operator fun invoke(a1: A1, a2: A2)
}

interface SuspendableUseCase3n<A1, A2, A3> {

    suspend operator fun invoke(a1: A1, a2: A2, a3: A3)
}

interface SuspendableUseCase4n<A1, A2, A3, A4> {

    suspend operator fun invoke(a1: A1, a2: A2, a3: A3, a4: A4)
}

interface SuspendableUseCase5n<A1, A2, A3, A4, A5> {

    suspend operator fun invoke(a1: A1, a2: A2, a3: A3, a4: A4, a5: A5)
}
