package com.appnroll.mvi.di

import com.appnroll.mvi.common.DefaultSchedulersProvider
import com.appnroll.mvi.common.SchedulersProvider
import org.koin.core.module.Module

inline val Module.SchedulersModule
    get() = configure {
        single<SchedulersProvider> { DefaultSchedulersProvider.instance }
    }