package com.appnroll.mvi.ui.base.mvi

import io.reactivex.Scheduler


interface SchedulersProvider {

    fun subscriptionScheduler(): Scheduler

    fun observationScheduler(): Scheduler
}