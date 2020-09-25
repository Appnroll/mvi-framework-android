package com.appnroll.mvi.ui.components.home.mvi

import androidx.lifecycle.SavedStateHandle
import com.appnroll.mvi.common.mvi.MviController
import com.appnroll.mvi.common.mvi.internal.MviViewStateCacheImpl
import com.appnroll.mvi.common.mvi.model.MviActionProcessingFlow
import com.appnroll.mvi.common.mvi.state.MviStateProcessingFlow
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction
import com.appnroll.mvi.ui.components.home.mvi.model.HomeActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult
import com.appnroll.mvi.ui.components.home.mvi.state.HomeStateReducer
import com.appnroll.mvi.ui.components.home.mvi.state.HomeViewState
import kotlinx.coroutines.CoroutineScope

/**
 * This is a kind of boilerplate code which is written in order to provide a strong types for all
 * mvi related classes from one feature. It is done in order to simplify dependency injection which
 * in case of Koin relies on class types.
 *
 * An alternative solution is to use Koin named() qualifiers, in example:
 * ```
 * factory(named("HomeActionProcessor")) {
 *     HomeActionProcessor(...)
 * }
 *
 * factory(named("HomeActionProcessingFLow")) {
 *     MviActionProcessingFlow<MviAction, MviResult>(
 *         mviActionProcessor = get(named("HomeActionProcessor"))
 *     )
 * }
 *
 * factory(named("HomeMviController")) { (...) ->
 *     MviController<MviAction, MviResult, MviViewState>(
 *         mviActionProcessingFlow = get(named("HomeActionProcessingFLow")),
 *         ...
 *     )
 * }
 * ```
 * However (in my opinion) providing strong type is less error prone then named() qualifiers.
 */

class HomeMviController(
    homeActionProcessingFlow: HomeActionProcessingFlow,
    homeStateProcessingFlow: HomeStateProcessingFlow,
    homeViewStateCache: HomeViewStateCache,
    coroutineScope: CoroutineScope
) : MviController<HomeAction, HomeResult, HomeViewState>(
    mviActionProcessingFlow = homeActionProcessingFlow,
    mviStateProcessingFlow = homeStateProcessingFlow,
    mviViewStateCache = homeViewStateCache,
    coroutineScope = coroutineScope
)

class HomeActionProcessingFlow(
    homeActionProcessor: HomeActionProcessor
): MviActionProcessingFlow<HomeAction, HomeResult>(
    mviActionProcessor = homeActionProcessor
)

class HomeStateProcessingFlow(
    homeStateReducer: HomeStateReducer,
    homeViewStateCache: HomeViewStateCache
) : MviStateProcessingFlow<HomeResult, HomeViewState>(
    mviViewStateCache = homeViewStateCache,
    mviStateReducer = homeStateReducer
)

class HomeViewStateCache(
    savedStateHandle: SavedStateHandle
): MviViewStateCacheImpl<HomeViewState>(
    key = "HomeViewStateKey",
    savedStateHandle = savedStateHandle
)

