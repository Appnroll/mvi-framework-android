package com.appnroll.mvi.utils

import com.appnroll.mvi.ui.base.mvi.SchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class AndroidSchedulerProvider private constructor() : SchedulerProvider {

    override fun io(): Scheduler = Schedulers.io()

    override fun ui(): Scheduler = AndroidSchedulers.mainThread()

    companion object {

        val instance = AndroidSchedulerProvider()
    }
}