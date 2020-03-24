package com.appnroll.mvi.common

import io.reactivex.Scheduler


interface SchedulersProvider {

    fun subscriptionScheduler(): Scheduler

    fun observationScheduler(): Scheduler
}