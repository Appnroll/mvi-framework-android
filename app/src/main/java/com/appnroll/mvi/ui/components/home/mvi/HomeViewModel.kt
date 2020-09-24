package com.appnroll.mvi.ui.components.home.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appnroll.mvi.common.mvi.MviController
import com.appnroll.mvi.ui.components.home.mvi.model.HomeAction
import com.appnroll.mvi.ui.components.home.mvi.model.HomeResult
import com.appnroll.mvi.ui.components.home.mvi.state.HomeViewState

class HomeViewModel(
    val homeMviController: MviController<HomeAction, HomeResult, HomeViewState>
) : ViewModel() {

    init {
        homeMviController.init(viewModelScope)
    }
}
