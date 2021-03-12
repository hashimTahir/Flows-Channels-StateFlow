/*
 * Copyright (c) 2021/  3/ 12.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow.flows

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import kotlin.system.measureTimeMillis


/*
* Buffering﻿
* Running different parts of a flow in different coroutines can be helpful from the
* standpoint of the overall time it takes to collect the flow, especially when
* long-running asynchronous operations are involved. For example,
* when the emission by a simple flow is slow, taking 100 ms to produce an element;
*  and collector is also slow, taking 300 ms to process an element.
* The Total time of execution  would be: Takes about 1233ms
* */




fun hTimeForExecution(): Flow<Int> = flow {
    for (i in 1..3) {
        delay(100) // pretend we are asynchronously waiting 100 ms
        emit(i) // emit next value
    }
}


@InternalCoroutinesApi
fun hRunnerTimeForExecution() = runBlocking {

    val hTime = measureTimeMillis {
        hTimeForExecution().collect {
            delay(300) // pretend we are processing it for 300 ms
            Timber.d("Collected value $it")
        }
    }

    Timber.d("Collected in $hTime ms")
}
////////////////////////////////////////////////////////////////////////////////////////


/*
* Buffer operator can be use on a flow to run emitting code of the hBufferingFlow
*  concurrently with collecting code, as opposed to running them sequentially:
*
* It produces the same numbers but faster, as processing pipeline has been created,
*  having to only wait 100 ms for the first number and then spending only
* 300 ms to process each number. Takes about 1000ms to complete.:
* */


fun hRunnerBufferFlow() = runBlocking {

    val hTime = measureTimeMillis {
        hTimeForExecution()
            .buffer()  //buffer emissions, don't wait
            .collect {
                delay(300) // pretend we are processing it for 300 ms
                Timber.d("Collected value $it")
            }
    }

    Timber.d("Collected in $hTime ms")
}



