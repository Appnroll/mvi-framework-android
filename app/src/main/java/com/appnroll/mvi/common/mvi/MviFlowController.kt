package com.appnroll.mvi.common.mvi

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appnroll.mvi.common.mvi.model.MviAction
import com.appnroll.mvi.common.mvi.model.MviResult
import com.appnroll.mvi.common.mvi.ui.MviStateController
import com.appnroll.mvi.common.mvi.ui.MviViewState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

abstract class MviFlowController<A : MviAction, R : MviResult, VS : MviViewState>(
    private val savedStateHandle: SavedStateHandle,
    private val modelController: MviController<A, R>,
    stateControllerBuilder: (initial: VS?) -> MviStateController<R, VS>
) : ViewModel() {

    private val stateController: MviStateController<R, VS> = stateControllerBuilder(
        savedStateHandle.get<VS>(VIEW_STATE_KEY)
    )

    val state: Flow<VS> = stateController

    init {
        modelController.onEach(stateController::accept).launchIn(viewModelScope)
        stateController.savable
            .onEach { savedStateHandle.set<VS>(VIEW_STATE_KEY, it) }
            .launchIn(viewModelScope)
    }

    fun accept(intent: VS.() -> A?) {
        viewModelScope.launch {
            modelController.accept(
                intent(stateController.current) ?: return@launch
            )
        }
    }

    companion object {
        private const val VIEW_STATE_KEY = "ViewStateKey"
    }
}