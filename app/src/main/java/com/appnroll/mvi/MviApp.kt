package com.appnroll.mvi

import android.app.Application
import com.appnroll.mvi.di.appModule
import com.appnroll.mvi.di.repositoriesModule
import com.appnroll.mvi.di.roomModule
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
            modules(appModule, roomModule, repositoriesModule)
        }
    }

    companion object {

        @JvmStatic
        lateinit var instance: MviApp
            private set
    }
}