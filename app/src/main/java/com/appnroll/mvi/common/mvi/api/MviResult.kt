package com.appnroll.mvi.common.mvi.api

/**
 * The partial outcome of processing an MviAction (by MviActionProcessor).
 *
 * By convention - if processing an MviAction includes some async call (like API or DB call) then it
 * should starts with producing `InProgress` result and ends with producing `Success` result
 * (or `Failure` result in case of exception/error).
 */
interface MviResult