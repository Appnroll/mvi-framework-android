package com.appnroll.mvi.di

import com.appnroll.mvi.ui.base.mvi.DefaultSchedulersProvider
import com.appnroll.mvi.ui.base.mvi.SchedulersProvider
import org.koin.core.module.Module

inline val Module.SchedulersModule
    get() = configure {
        single<SchedulersProvider> { DefaultSchedulersProvider.instance }
    }