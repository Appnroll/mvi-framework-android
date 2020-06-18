package com.appnroll.mvi.utils

import android.os.Bundle
import androidx.savedstate.SavedStateRegistryOwner
import com.appnroll.mvi.common.mvi.MviFlowController
import org.koin.androidx.viewmodel.ext.android.getStateViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier

inline fun <reified T : MviFlowController<*, *, *>> SavedStateRegistryOwner.mviController(
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    noinline parameters: ParametersDefinition? = null
): Lazy<T> {
    return lazy { getStateViewModel(T::class, qualifier, bundle, parameters) }
}