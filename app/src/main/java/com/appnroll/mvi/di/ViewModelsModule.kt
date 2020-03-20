package com.appnroll.mvi.di

import androidx.lifecycle.SavedStateHandle
import com.appnroll.mvi.ui.components.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module

inline val Module.ViewModels
    get() = configure {
        viewModel { (handle: SavedStateHandle) -> HomeViewModel(handle) }
    }