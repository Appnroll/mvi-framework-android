package com.appnroll.mvi.ui.base.mvi

import androidx.appcompat.app.AppCompatActivity

abstract class MviBaseActivity<
        ActionType: MviAction,
        ResultType: MviResult,
        ViewStateType: MviViewState<ResultType>,
        ViewModelType: MviViewModel<ActionType, ResultType, ViewStateType>>(
    viewModelClass: Class<ViewModelType>
): AppCompatActivity() {

    private val mviViewModelWrapper by lazy { MviViewModelWrapper(this, viewModelClass) }

    @Suppress("UNCHECKED_CAST")
    val viewModel: ViewModelType by lazy { mviViewModelWrapper.viewModel as ViewModelType }
    
    override fun onStart() {
        super.onStart()
        mviViewModelWrapper.startProcessing(::render)
    }
    
    override fun onStop() {
        mviViewModelWrapper.stopProcessing()
        super.onStop()
    }
    
    abstract fun render(viewState: ViewStateType)
}