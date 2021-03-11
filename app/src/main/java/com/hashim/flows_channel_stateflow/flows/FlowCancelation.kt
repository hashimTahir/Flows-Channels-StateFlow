/*
 * Copyright (c) 2021/  3/ 11.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow.flows

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import timber.log.Timber

/*
Flow adheres to the general cooperative cancellation of coroutines.
As usual, flow collection can be cancelled when the flow is suspended in a cancellable
suspending function (like delay ). The following example shows how the flow gets cancelled
on a timeout when running in a withTimeoutOrNull block and stops executing its code:
 */




fun hCancellationFlow(): Flow<Int> = flow {

    for (i in 1..3) {
        delay(100)
        Timber.d("Emitting $i")
        emit(i)
    }
}

@InternalCoroutinesApi
fun hRunnerForCancellationFlow() = runBlocking {

    /*This times out after 250 millis*/
    withTimeoutOrNull(250) {
        // Timeout after 250ms
        hCancellationFlow().collect { Timber.d("Value is $it") }
    }
    Timber.d("Done")

}

//////////////////////////////////////////////////////////////////////////////

/*
* Only numbers up to 3 and a CancellationException after trying to emit number 4,
* Crashes at value =4*/
fun hConditionalCancelation(): Flow<Int> = flow {
    for (i in 1..5) {
        Timber.d("Emitting $i")
        emit(i)
    }
}

fun hRunnerForCondiationalCancelation() = runBlocking {
    hConditionalCancelation().collect { value ->
        if (value == 3) cancel()
        Timber.d("hRunnerForCondiationalCancelation $value")
    }
}


/*
* Most other flow operators do not do additional cancellation checks on
* their own for performance reasons. For example, if IntRange.asFlow is used as extension
*  to write the same busy loop and don't suspend anywhere, then there are no checks
* for cancellation:
* */
/*All numbers from 1 to 5 are collected and cancellation gets detected only
before return from runBlocking*/

fun hRunnerForRangeCancellation() = runBlocking<Unit> {
    (1..5).asFlow().collect { value ->
        if (value == 3) cancel()
        Timber.d("hRunnerForCondiationalCancelation $value")
    }
}
//////////////////////////////////////////////////////////////////////////////////


/*In the case of a busy loop with coroutines, explicit
check for cancellation is required. Add .onEach { currentCoroutineContext().ensureActive() },
but there is a ready-to-use cancellable operator provided to do that:*/

/*With the cancellable operator only the numbers from 1 to 3 are collected:*/
fun hRunnerForCancellable() = runBlocking<Unit> {
    (1..5).asFlow().cancellable().collect { value ->
        if (value == 3) cancel()
        Timber.d("hRunnerForCancellable $value")

    }
}