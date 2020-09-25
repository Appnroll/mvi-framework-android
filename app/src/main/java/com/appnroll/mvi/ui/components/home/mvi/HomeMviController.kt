package com.appnroll.mvi.ui.components.home.mvi

import androidx.lifecycle.SavedStateHandle
import com.appnroll.mvi.common.mvi.MviController
import com.appnroll.mvi.common.mvi.utils.MviViewStateCacheImpl
import com.appnroll.mvi.common.mvi.processing.MviActionProcessingFlow
import com.appnroll.mvi.common.mvi.processing.MviResultProcessingFlow
import com.appnroll.mvi.ui.components.home.mvi.impl.HomeAction
import com.appnroll.mvi.ui.components.home.mvi.impl.HomeActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.impl.HomeResult
import com.appnroll.mvi.ui.components.home.mvi.impl.HomeResultReducer
import com.appnroll.mvi.ui.components.home.mvi.impl.HomeViewState
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
    homeResultProcessingFlow: HomeResultProcessingFlow,
    homeViewStateCache: HomeViewStateCache,
    coroutineScope: CoroutineScope
) : MviController<HomeAction, HomeResult, HomeViewState>(
    mviActionProcessingFlow = homeActionProcessingFlow,
    mviResultProcessingFlow = homeResultProcessingFlow,
    mviViewStateCache = homeViewStateCache,
    coroutineScope = coroutineScope
)

class HomeActionProcessingFlow(
    homeActionProcessor: HomeActionProcessor
) : MviActionProcessingFlow<HomeAction, HomeResult>(
    mviActionProcessor = homeActionProcessor
)

class HomeResultProcessingFlow(
    homeResultReducer: HomeResultReducer,
    homeViewStateCache: HomeViewStateCache
) : MviResultProcessingFlow<HomeResult, HomeViewState>(
    mviViewStateCache = homeViewStateCache,
    mviResultReducer = homeResultReducer
)

class HomeViewStateCache(
    savedStateHandle: SavedStateHandle
) : MviViewStateCacheImpl<HomeViewState>(
    key = "HomeViewStateKey",
    savedStateHandle = savedStateHandle
)
