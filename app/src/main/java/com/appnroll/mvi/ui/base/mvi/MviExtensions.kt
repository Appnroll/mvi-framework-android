package com.appnroll.mvi.ui.base.mvi

import androidx.savedstate.SavedStateRegistryOwner
import com.appnroll.mvi.ui.components.home.HomeViewState
import com.appnroll.mvi.ui.components.home.mvi.HomeAction
import com.appnroll.mvi.ui.components.home.mvi.HomeResult
import org.koin.androidx.viewmodel.ext.android.getStateViewModel
import org.koin.core.qualifier.named

fun SavedStateRegistryOwner.mviStateProcessor(viewModelName: String):
        Lazy<MviStateProcessor<HomeAction, HomeViewState>> = lazy {
    getStateViewModel<MviViewModel<HomeAction, HomeResult, HomeViewState>>(
        qualifier = named(viewModelName)
    ).stateReducer
}