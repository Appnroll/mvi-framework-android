package com.appnroll.mvi.di

import androidx.lifecycle.SavedStateHandle
import com.appnroll.mvi.ui.components.home.mvi.HomeFlowController
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module

inline val Module.Processors
    get() = configure {
        /**
         * TODO: remove hack from processors and make configure them here
         *
         * */
    }

inline val Module.ViewModels
    get() = configure {
        viewModel { (handle: SavedStateHandle) ->
            HomeFlowController(
                handle
            )
        }
    }