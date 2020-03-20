package com.appnroll.mvi.di

import org.koin.dsl.module

val appModule = module {
    SchedulersModule
    ViewModels
    RepositoryModule
    RoomModule
}