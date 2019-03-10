package com.appnroll.mvi

import android.app.Application
import com.appnroll.mvi.di.appModule
import com.appnroll.mvi.di.repositoriesModule
import com.appnroll.mvi.di.roomModule
import org.koin.android.ext.android.startKoin


class MviApp: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        initKoin()
    }

    private fun initKoin() {
        startKoin(this, listOf(appModule, roomModule, repositoriesModule))
    }

    companion object {

        @JvmStatic
        lateinit var instance: MviApp
            private set
    }
}