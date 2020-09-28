package com.appnroll.mvi.common.mvi.api

/**
 * Action (aka Intent) in MVI pattern is the starting point for a process which will result
 * in updating UI with new ViewState.
 * Each Action could represents:
 * a) User interaction with an app - button click, swipe, tab change, etc.
 * b) System events - onStart(), onPause(), onViewCreated(), etc.
 * c) Timer event
 *
 * By convention - different MviActions which are proceed in the same way should be merged into one,
 * i e.g.: instead of two separate actions: OnLoadDataButtonClickAction and OnSwipeToRefreshAction -
 * there should be only one: LoadDataAction, and it should be send both after click on the button
 * and after "swipe to refresh" event.
 */
interface MviAction {

    /**
     * Parameter which defines a uniqueness of an action object instance.
     * Only one MviAction with a specific Id could be proceeded at once.
     *
     * Default usage:
     * By default class name is used as an Id. Consider two actions object `a1` and `a2`
     * of the class A. When `a2` is send during processing of `a1`, then processing of `a1`
     * is canceled and `a2` is started to process.
     *
     * Customized usage:
     * Consider MviAction class B:
     * class B(itemId: Int): MviAction { override fun getId() = super.getId() + itemId }
     * Having three action objects: `b1(itemId=1)`, `b2(itemId=2) and `b3(itemId=1)` and sending
     * `b1` and `b2` will cause the parallel processing of those actions - as they are both unique.
     * Sending `b3` will cancel the processing of `b1` action but `b2` will still be processing.
     *
     * @throws IllegalStateException if method is used with anonymous class
     */
    fun getId(): Any = this::class.qualifiedName
        ?: throw IllegalStateException("getId() should not be used with anonymous classes")
}
