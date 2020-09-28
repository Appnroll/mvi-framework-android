package com.appnroll.mvi.common.mvi.api

/**
 * Describes how one MviResult object instance is reduced with current MviViewState.
 */
interface MviResultReducer<R : MviResult, VS : MviViewState> {

    /**
     * Returns initial (default) MviViewState.
     */
    fun default(): VS

    /**
     * Reduces the latest MviViewState with the MviResult.
     */
    fun VS.reduce(result: R): VS

    /**
     * Fold MviViewState before it is saved to ViewStateCache.
     * In this function heavy object references should be cleared because the default cache relies
     * on Android Bundle object and its maximum size is restricted.
     */
    fun fold(current: VS): VS? = current
}