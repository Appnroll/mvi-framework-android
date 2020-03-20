package com.appnroll.mvi

import android.app.Application
import com.appnroll.mvi.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


class MviApp: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        initKoin()
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@MviApp)
            modules(
                appModule
            )
        }
    }

    companion object {

        @JvmStatic
        lateinit var instance: MviApp
            private set
    }
}