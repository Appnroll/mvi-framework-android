package com.appnroll.mvi.di

import androidx.lifecycle.SavedStateHandle
import com.appnroll.mvi.ui.base.mvi.MviViewModel
import com.appnroll.mvi.ui.components.home.HomeFragment
import com.appnroll.mvi.ui.components.home.HomeViewResultReducer
import com.appnroll.mvi.ui.components.home.mvi.HomeActionProcessor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.core.qualifier.named

inline val Module.ViewModels
    get() = configure {
        viewModel(qualifier = named(HomeFragment.ViewModelName)) { (handle: SavedStateHandle) ->
            MviViewModel(
                handle,
                HomeViewResultReducer(),
                HomeActionProcessor()
            )
        }
    }