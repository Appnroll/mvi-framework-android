package com.appnroll.mvi.di

import org.koin.core.module.Module

inline val Module.SchedulersModule
    get() = configure {
        /*
        * TODO: processors should obtain scopes / dispatchers from dependencies
        * */
    }