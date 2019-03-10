package com.appnroll.mvi.di

import com.appnroll.mvi.ui.base.mvi.SchedulerProvider
import com.appnroll.mvi.utils.AndroidSchedulerProvider
import org.koin.dsl.module.module


val appModule = module {

    single<SchedulerProvider> { AndroidSchedulerProvider.instance }

}