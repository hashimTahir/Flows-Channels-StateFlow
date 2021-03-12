/*
 * Copyright (c) 2021/  3/ 12.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow.flows

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber


//////////////////////////////////////////////////////////////////////////////////////////////////
/*
* Collection of a flow always happens in the context of the calling coroutine.
* This property of a flow is called context preservation.
* */


fun hGetThreadName(msg: String) = "Thread name [${Thread.currentThread().name}] message: $msg"

private fun hFlowsThreadName(): Flow<Int> = flow {
    Timber.d("Started simple flow")
    for (i in 1..3) {
        emit(i)
    }
}

/*Called from main activity main thread context, so all vales must be collected
* in the same context.*/
@InternalCoroutinesApi
fun hRunnerForFlowsThreadName() = runBlocking {

    hFlowsThreadName().collect { value ->
        Timber.d(hGetThreadName(" Collected $value"))
    }
}
//////////////////////////////////////////////////////////////////////////////////////////////////


/*
* Wrong emission withContext
* However, the long-running CPU-consuming code might need to be executed in
* the context of Dispatchers.Default and UI-updating code might need to
* be executed in the context of Dispatchers.Main. Usually, withContext
* is used to change the context in the code using Kotlin coroutines, but
*  code in the flow { ... } builder has to honor the context preservation
*  property and is not allowed to emit from a different context.
* */

fun hWrongFlowEmission(): Flow<Int> = flow {
    // The WRONG way to change context for CPU-consuming code in flow builder
    withContext(Dispatchers.Default) {
        for (i in 1..3) {
            Thread.sleep(100) // pretend we are computing it in CPU-consuming way
            emit(i) // emit next value
        }
    }
}

/*Called from Main but hWrongFlow Emits from Dispaters.Default
* crashes with exception.
* */
fun hRunnerForWrongEmission() = runBlocking {
    hWrongFlowEmission().collect { value ->
        Timber.d(hGetThreadName("value $value"))
    }
}

//////////////////////////////////////////////////////////////////////////////////////////////////


/*
*FlowOn Operator is used to change the context
*The correct way to change the context of a flow is as follows,
*
*
* The FlowOn operator has changed
* the default sequential nature of the flow. Now collection happens in one
*  coroutine and emission happens in another coroutine
* that is running in another thread concurrently with the
*  collecting coroutine. The flowOn operator creates another coroutine for
*  an upstream flow when it has to change the CoroutineDispatcher in its context.
* */

fun hFlowOnToSwitchThread(): Flow<Int> = flow {
    for (i in 1..3) {
        Thread.sleep(100) // pretend we are computing it in CPU-consuming way
//        Print the thread name where values are emitted
        Timber.d(hGetThreadName("Emitting $i"))
        emit(i) // emit next value
    }
}.flowOn(Dispatchers.Default) // RIGHT way to change context for CPU-consuming code in flow builder


/*Called from main, so Runner should print the main thread*/
fun hRunnerForFlowOnToSwitchThread() = runBlocking {
    hFlowOnToSwitchThread().collect { value ->
//        Print the thread name where the values are collected
        Timber.d(hGetThreadName("Collected $value"))

    }
}