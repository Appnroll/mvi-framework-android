package com.appnroll.mvi.utils

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.appnroll.mvi.common.mvi.MviController
import org.koin.androidx.viewmodel.ext.android.getStateViewModel
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import kotlin.reflect.KClass

/**
 * Instantiate ViewModel and provides MviController reference from this ViewModel
 */
inline fun <reified VM : ViewModel, reified C : MviController<*, *, *>> SavedStateRegistryOwner.mviController(
    clazz: KClass<VM>,
    qualifier: Qualifier? = null,
    bundle: Bundle? = null,
    noinline parameters: ParametersDefinition? = null,
    crossinline mviControllerProvider: VM.() -> C
): Lazy<C> {
    return lazy { getStateViewModel(clazz, qualifier, bundle, parameters).mviControllerProvider() }
}