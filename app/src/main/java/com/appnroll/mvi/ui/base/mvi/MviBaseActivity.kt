package com.appnroll.mvi.ui.base.mvi

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

abstract class MviBaseActivity<
        ActionType: MviAction,
        ResultType: MviResult,
        ViewStateType: MviViewState<ResultType>,
        ViewModelType: MviViewModel<ActionType, ResultType, ViewStateType>>(
    private val viewModelClass: Class<ViewModelType>
): AppCompatActivity(), MviControllerCallback<ActionType, ResultType, ViewStateType> {

    protected val mviController by lazy {
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
        mviController.initViewStatesObservable(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        mviController.saveLastViewState(outState)
        super.onSaveInstanceState(outState)
    }
}