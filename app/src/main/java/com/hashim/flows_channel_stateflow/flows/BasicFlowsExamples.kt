/*
 * Copyright (c) 2021/  3/ 11.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber


//////////////////////////////////////////////////////////////////////////////////////////////
/*
   * This computation blocks the main thread that is running the code.
   * */
private fun ThreadBlockingBlock(): Sequence<Int> = sequence {
    for (i in 1..3) {
        Thread.sleep(1000)
        yield(i)
    }
}


fun hRunnerForThreadBlockingBlock() {
    ThreadBlockingBlock().forEach { value -> Timber.d("Value is $value") }
}
//////////////////////////////////////////////////////////////////////////////////////////////

private suspend fun NonUiThreadBlocking(): List<Int> {
    delay(1000) // pretend we are doing something asynchronous here
    return listOf(1, 2, 3)
}

fun hRunnerForNonUiThreadBlocking() = runBlocking<Unit> {
    NonUiThreadBlocking().forEach { value -> Timber.d("Value is $value") }
}
//////////////////////////////////////////////////////////////////////////////////////////////


/*
* Using the List<Int> result type, means all the values are returned at once.
*  To represent the stream of values that are being asynchronously computed,
* Flow<Int> type is used
* This code waits 2000ms before printing each number without blocking the main thread.
* */


/*
*     A builder function for Flow type is called flow.
* Code inside the flow { ... } builder block can suspend.
* The simple function is no longer marked with suspend modifier.
* Values are emitted from the flow using emit function.
* Values are collected from the flow using collect function.
*
* Flows are cold streams similar to sequences — the code inside a flow builder
* does not run until the flow is collected.
*
*
* This is a key reason the hCreateFlow function (which returns a flow) is not marked with suspend modifier.
*  By itself, hCreateFlow() call returns quickly and does not wait for anything.
* The flow starts every time it is collected, that is why,
*  "Flow started" is printed again when its collected agian.
*
* */
private fun hCreateFlow(): Flow<Int> = flow { // flow builder
    for (i in 1..3) {
        delay(2000) // pretend we are doing something useful here
        emit(i) // emit next value
    }
}

fun hRunnerForFlow() = runBlocking<Unit> {
    // Launch a concurrent coroutine to check if the main thread is blocked
    launch {
        for (k in 1..5) {
            Timber.d("Not Blocked  is $k")
            delay(100)
        }
    }
    // Collect the flow
    hCreateFlow().collect { Timber.d("Value is $it") }
}



