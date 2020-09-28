package com.appnroll.mvi.common.mvi.api

/**
 *
 * Business Logic Flow Loop:
 *  Action -> Processor -> Flow<Result>
 *
 * */

/*
* Logic base definitions
* */
interface MviAction {

    fun getId(): Any = this.javaClass.simpleName
}
