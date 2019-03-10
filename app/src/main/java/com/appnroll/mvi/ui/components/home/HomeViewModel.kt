package com.appnroll.mvi.ui.components.home

import com.appnroll.mvi.ui.base.mvi.MviViewModel
import com.appnroll.mvi.ui.components.home.mvi.HomeAction
import com.appnroll.mvi.ui.components.home.mvi.HomeActionProcessor
import com.appnroll.mvi.ui.components.home.mvi.HomeResult


class HomeViewModel: MviViewModel<HomeAction, HomeResult, HomeViewState>(
    HomeActionProcessor(),
    HomeViewState.default()
)