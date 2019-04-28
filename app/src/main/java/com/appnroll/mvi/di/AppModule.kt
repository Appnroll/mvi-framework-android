package com.appnroll.mvi.di

import com.appnroll.mvi.ui.base.mvi.SchedulersProvider
import com.appnroll.mvi.utils.DefaultSchedulersProvider
import org.koin.dsl.module.module


val appModule = module {

    single<SchedulersProvider> { DefaultSchedulersProvider.instance }

}