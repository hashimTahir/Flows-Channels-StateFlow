/*
 * Copyright (c) 2021/  3/ 11.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow.flows

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
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

    withTimeoutOrNull(250) {
        // Timeout after 250ms
        hCancellationFlow().collect { Timber.d("Value is $it") }
    }
    Timber.d("Done")

}