package com.appnroll.mvi.utils

import androidx.savedstate.SavedStateRegistryOwner
import com.appnroll.mvi.ui.base.mvi.MviAction
import com.appnroll.mvi.ui.base.mvi.MviStateController
import com.appnroll.mvi.ui.base.mvi.MviViewModel
import com.appnroll.mvi.ui.base.mvi.MviViewState
import com.appnroll.mvi.ui.base.mvi.stateController
import org.koin.androidx.viewmodel.ext.android.getStateViewModel
import org.koin.core.qualifier.named

fun <A : MviAction, S : MviViewState> SavedStateRegistryOwner.mviStateProcessor(viewModelName: String):
        Lazy<MviStateController<A, S>> = lazy {
    getStateViewModel<MviViewModel<A, *, S>>(
        qualifier = named(viewModelName)
    ).stateController
}