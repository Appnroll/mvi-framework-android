package com.appnroll.mvi.ui.components.home.mvi

import com.appnroll.mvi.common.mvi.api.MviResultReducer

/**
 * Reducer which creates new ViewState based on the last ViewState and new Result
 */
class HomeResultReducer : MviResultReducer<HomeViewState> {

    override fun default(): HomeViewState =
        HomeViewState(
            inProgress = false,
            tasks = null,
            newTaskAdded = null,
            error = null
        )
}
