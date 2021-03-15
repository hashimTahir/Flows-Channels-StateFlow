/*
 * Copyright (c) 2021/  3/ 15.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow.stateflow


/*

State Flow
* StateFlow is a state-holder observable flow that emits the current and new state
*  updates to its collectors. The current state value can also be read through its
* value property. To update state and send it to the flow, assign a new value to the
* value property of the MutableStateFlow class.

In Android, StateFlow is a great fit for classes that need to maintain an observable mutable state.

*
*
*
*
* The class responsible for updating a MutableStateFlow is the producer, and all
*  classes collecting from the StateFlow are the consumers.
* UNLIKE A COLD FLOW BUILD USING THE FLOW BUILDER, A STATEFLOW IS HOT:
* COLLECTING FROM FLOW DOSENT TRIGGER ANY PRODUCER CODE. A STATE FLOW  IS ALWASYS ACTIVITY AND
* IN MEMORY.
*  and it becomes eligible for garbage collection only when there are no other references
*  to it from a garbage collection root.
*
* When a new consumer starts collecting from the flow, it receives the last state
* in the stream and any subsequent states. You can find this behavior in other observable
* classes like LiveData.

The View listens for StateFlow as with any other flow as in live data and mutable live data.
*
*
*
*
*
*
*
*

Important Note: Using launchWhen() functions from the Lifecycle Kotlin extensions
* to collect a flow from the UI layer is not always safe. When the view goes to
* the background, the coroutine suspends, leaving the underlying producer active
* and potentially emitting values that the view doesn't consume. This behavior
*  could waste CPU and memory resources.
*
* StateFlows are safe to collect using the launchWhen() functions since they're
* scoped to ViewModels, making them remain in memory when the View goes to the background,
*  and they do lightweight work by just notifying the View about UI states.
* However, the problem might come with other producers that do more intensive work.

Warning: Never collect a flow from the UI using launch or the launchIn extension
* function if the UI needs to be updated. These functions process events even when
*  the view is not visible. This behavior can lead to app crashes.

To convert any flow to a StateFlow, use the stateIn intermediate operator.
* */


/*
* Difference between  StateFlow, Flow and live data
*   1->StateFlow requires an initial state to be passed in to the constructor, while LiveData does not.
*
*   2->LiveData.observe() automatically unregisters the consumer when the view goes
*    to the STOPPED state, whereas collecting from a StateFlow or any other flow does not.
*
*
*   Another way to stop listening for uiState changes when the view is not visible is
*   to convert the flow to LiveData using the asLiveData() function from the
*   lifecycle-livedata-ktx library.
*
* */




/*Shared Flow*/

/*
* The shareIn function returns a SharedFlow, a hot flow that emits values to
* all consumers that collect from it. A SharedFlow is a highly-configurable
* generalization of StateFlow.
*
*  SharedFlow can be created without using shareIn. Eg.
* a SharedFlow can be used to send ticks to the rest of the app so that all the
* content refreshes periodically at the same time. Apart from fetching the latest news, one
*  might also want to refresh the user information section with its favorite topics collection.
*  In the following code snippet, a TickHandler exposes a SharedFlow so that other
* classes know when to refresh its content. As with StateFlow, use a backing property
*  of type MutableSharedFlow in a class to send items to the flow:
*
*
* Shared
*
* */