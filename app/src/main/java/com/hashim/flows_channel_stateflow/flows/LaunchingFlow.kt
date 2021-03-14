/*
 * Copyright (c) 2021/  3/ 14.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow.flows

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import timber.log.Timber


/*
* The required parameter to launchIn must specify a CoroutineScope in which the
* coroutine to collect the flow is launched. In the below example this scope
*  comes from the runBlocking coroutine builder, so while the flow is running,
*  this runBlocking scope waits for completion of its child coroutine and keeps
* the main function from returning and terminating this example.
*
*
* In actual applications a scope will come from an entity with a limited lifetime.
*  As soon as the lifetime of this entity is terminated the corresponding scope is cancelled,
* cancelling the collection of the corresponding flow. This way the
* pair of onEach { ... }.launchIn(scope) works like the addEventListener. However, there is
* no need for the corresponding removeEventListener function, as cancellation
* and structured concurrency serve this purpose.
*
* launchIn also returns a Job, which can be used to cancel the corresponding
* flow collection coroutine only without cancelling the whole scope or to join it.
*
*
* */

private fun hEmitFlow(): Flow<Int> = flow {

    for (i in 1..3) {
        delay(100)
        Timber.d("Emitting $i")
        emit(i)
    }
}

@InternalCoroutinesApi
fun hRunnerForCollect() = runBlocking<Unit> {
    hEmitFlow()
        .onEach { flow -> Timber.d("OnEach: $flow") }
        .collect()// <--- Collecting the flow waits
    Timber.d("Done")
}

/*
* The launchIn terminal operator.
*  By replacing collect with launchIn we can launch a collection of the flow in
*  a separate coroutine, so that execution of further code immediately continues:
* */
@InternalCoroutinesApi
fun hRunnerForLaunchIn() = runBlocking<Unit> {
    hEmitFlow()
        .onEach { event -> Timber.d("Event: $event") }
        .launchIn(this) // <--- Launching the flow in a separate coroutine
    Timber.d("Done")
}
///////////////////////////////////////////////////////////////////////////////


/*
* Flow cancellation checks
For convenience, the flow builder performs additional ensureActive checks for
* cancellation on each emitted value. It means that a busy loop emitting from a flow
* is cancellable:
*
* */

fun hEmitFlows(): Flow<Int> = flow {
    for (i in 1..5) {
        Timber.d("Emitting $i")
        emit(i)
    }
}

fun hRunnerForCancellableFlow() = runBlocking<Unit> {
    hEmitFlows().collect { value ->
        if (value == 3) cancel()
        Timber.d("Value is $value")
    }
}


/*
* However, most other flow operators do not do additional cancellation checks on
*  their own for performance reasons. For example, if same busy loop is used with
*  IntRange.asFlow extension don't suspend anywhere, then there are no checks for cancellation:
*
* */


fun hRunnerForRangeFlow() = runBlocking {
    /*does not get cancelled, at end cancel exception is thrown*/
    (1..5).asFlow().collect { value ->
        if (value == 3) cancel()
        Timber.d("Value$value")
    }
}