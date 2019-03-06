package com.appnroll.mvi

import android.app.Application


class MviApp: Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {

        @JvmStatic
        lateinit var instance: MviApp
            private set
    }
}