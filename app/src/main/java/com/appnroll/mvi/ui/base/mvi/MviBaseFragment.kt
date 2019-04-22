package com.appnroll.mvi.ui.base.mvi

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders

abstract class MviBaseFragment<
        ActionType: MviAction,
        ResultType: MviResult,
        ViewStateType: MviViewState<ResultType>,
        ViewModelType: MviViewModel<ActionType, ResultType, ViewStateType>>(
    private val viewModelClass: Class<ViewModelType>
): Fragment(), MviControllerCallback<ActionType, ResultType, ViewStateType> {

    private val mviController by lazy {
        MviController(
            ViewModelProviders.of(this),
            javaClass.name,
            lifecycle,
            this
        )
    }

    @Suppress("UNCHECKED_CAST")
    val viewModel: ViewModelType by lazy { mviController.viewModel as ViewModelType }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mviController.initViewModel(viewModelClass)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mviController.initViewStatesObservable(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mviController.saveLastViewState(outState)
        super.onSaveInstanceState(outState)
    }
}