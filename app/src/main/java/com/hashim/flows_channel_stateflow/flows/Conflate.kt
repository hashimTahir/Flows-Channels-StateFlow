/*
 * Copyright (c) 2021/  3/ 13.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow.flows

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import kotlin.system.measureTimeMillis


/*
* Conflation
* When a flow represents partial results of the operation or operation status updates,
*  it may not be necessary to process each value, but instead, only most recent ones.
* In this case, the conflate operator can be used to skip intermediate values when
* a collector is too slow to process them.
* */



/*
* while the first number was still being processed the second,
*  and third were already produced, so the second one was conflated and only the
* most recent (the third one) was delivered to the collector*/
@InternalCoroutinesApi
fun hRunnerForConflateFlow() = runBlocking {
    val hTime = measureTimeMillis {
        hTimeForExecution()
            .conflate() // conflate emissions, don't process each one
            .collect {
                delay(300)
                Timber.d("Collected value $it")
            }
    }
    Timber.d("Collected in $hTime ms")
}

//////////////////////////////////////////////////////////////////////////////////


/*Processing the latest value
*Conflation is one way to speed up processing when both the emitter and collector are slow.
*  It does it by dropping emitted values. The other way is to cancel a slow collector and
* restart it every time a new value is emitted. There is a family of xxxLatest operators
* that perform the same essential logic of a xxx operator, but cancel the code in their block
* on a new value:*/



/*
* Since the body of collectLatest takes 300 ms, but new values are emitted every 100 ms,
*  block is run on every value, but completes only for the last value:
* */

fun hRunnerForCollectLatestFlow() = runBlocking {
    val hTime = measureTimeMillis {
        hTimeForExecution()
            .collectLatest {
                Timber.d("Collecting $it")
                delay(300)
                Timber.d("Done value $it")
            }
    }
    Timber.d("Collected in $hTime ms")
}