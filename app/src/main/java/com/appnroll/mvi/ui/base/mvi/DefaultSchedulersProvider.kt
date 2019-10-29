package com.appnroll.mvi.ui.base.mvi

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class DefaultSchedulersProvider private constructor() : SchedulersProvider {

    override fun subscriptionScheduler(): Scheduler = Schedulers.io()

    override fun observationScheduler(): Scheduler = AndroidSchedulers.mainThread()

    companion object {

        val instance = DefaultSchedulersProvider()
    }
}