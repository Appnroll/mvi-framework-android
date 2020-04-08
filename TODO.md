
val homeMviFlow = mviFlow<Action, Result> {
    LoadTasksAction connect loadTasksActionProcessor

//    ...
}

val HomeReduce = mviReducer { result ->
    val a = reducer{

    }

    val a = reducer{

    }

    val a = reducer{

    }
    val a = reducer{

    }

    val a = reducer{

    }
    val a = reducer{

    }



    when (result) {
        is InProgressResult -> a
        is ErrorResult -> b
        is LoadTasksResult -> c
        is AddTaskResult -> d
        is UpdateTaskResult -> reduce(current, result)
        is DeleteCompletedTasksResult -> reduce(current, result)
    }
}


data class Feature1State: Fragment1State, Fragment2State(
    interface Fragment1State{
        val 1 = 0,
        val 2,
    }

    interface Fragment2State{
        val 3,
        val 4,
    }    
        
    val 5,
    val 6
)


data class Fragment1State(
    state: Feature1State,
    val localState = state.transform()
)

val scopedState = object: Fragment1State by featureState




val homeUiFlow: MviUiFlow<Action, ViewState> = mviUiFlow {
    homeMviFlow using HomeReduce
}

val dialogUiFlow = mviUiFlow {
    homeMviFlow using mviReducer { result ->
        when (result) {
            is InProgressResult -> (State).{ result -> HomeViewState() }
            is ErrorResult -> reduce(current, result)
            is LoadTasksResult -> reduce(current, result)
            is AddTaskResult -> reduce(current, result)
            is UpdateTaskResult -> reduce(current, result)
            is DeleteCompletedTasksResult -> reduce(current, result)
        }
        LoadTasksResult reduceTo
        LoadTasksResult reduceTo HomeViewState()
        LoadTasksResult reduceTo HomeViewState()
        LoadTasksResult reduceTo HomeViewState()
        LoadTasksResult reduceTo HomeViewState()
        LoadTasksResult reduceTo HomeViewState()

        //...
    }
}

val dialog2UiFlow = mviUiFlow {
    homeMviFlow using mviReducer {
        LoadTasksResult reduceTo HomeViewState()

        //...
    }
}

val loadTasksActionProcessor = safeActionProcessor { action: LoadTasksAction ->

}

class LoadTaskActionProcessor : ActionProcessor<A, R> {
    fun declare() {
        //...
    }

    getBaseAction = LoadTasksAction
}