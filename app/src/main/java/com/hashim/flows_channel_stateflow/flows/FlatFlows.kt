/*
 * Copyright (c) 2021/  3/ 13.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow.flows

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/*
* Flattening flows
Flows represent asynchronously received sequences of values, so it is quite easy
*  to get in a situation where each value triggers a request for another sequence of values.
* For example,  the following function that returns a flow of two strings 500 ms apart:
*
*(1..3).asFlow().map { hRequestFlow(it) }
* Then we end up with a flow of flows (Flow<Flow<String>>) that needs to be flattened
*  into a single flow for further processing. Collections and sequences have flatten
*  and flatMap operators for this. However, due to the asynchronous nature of flows
* they call for different modes of flattening, as such, there is a family of
* flattening operators on flows.
* FlatMapConcat have he sequential nature.
* */

private fun hRequestFlow(i: Int): Flow<String> = flow {
    emit("$i: First")
    delay(500) // wait 500 ms
    emit("$i: Second")
}


/*flatMapConcat
*Concatenating mode is implemented by flatMapConcat and flattenConcat operators.
* They are the most direct analogues of the corresponding sequence operators.
*  They wait for the inner flow to complete before starting to collect the next one
* */
fun hRunnerForConcatFlatFlow() = runBlocking {
    val startTime = System.currentTimeMillis() // remember the start time
    (1..3).asFlow().onEach { delay(100) } // a number every 100 ms
        .flatMapConcat { hRequestFlow(it) }
        .collect { value -> // collect and print
            Timber.d("$value at ${System.currentTimeMillis() - startTime} ms from start")
        }
}


///////////////////////////////////////////////////////////////////////////////////


/*
* flatMapMerge
* Another flattening mode is to concurrently collect all the incoming flows
* and merge their values into a single flow so that values are emitted as soon as possible.
*  It is implemented by flatMapMerge and flattenMerge operators.
*  They both accept an optional concurrency parameter that limits the number of
* concurrent flows that are collected at the same time
* (it is equal to DEFAULT_CONCURRENCY by default).*/
/*flatMapMerge are concurent in nature.
* */

fun hRunnerForMergeFlatFlow() = runBlocking {
    val startTime = System.currentTimeMillis() // remember the start time
    (1..3).asFlow().onEach { delay(100) } // a number every 100 ms
        .flatMapMerge { hRequestFlow(it) }
        .collect { value -> // collect and print
            Timber.d("$value at ${System.currentTimeMillis() - startTime} ms from start")
        }
}
///////////////////////////////////////////////////////////////////////////////////////////////

/*
* flatMapLatest
* In a similar way to the collectLatest operator, there is the corresponding
* "Latest" flattening mode where
*  a collection of the previous flow is cancelled as soon as new flow is emitted.
* It is implemented by the flatMapLatest operator.
*
* */


fun hRunnerForLatestFlatFlow() = runBlocking {
    val startTime = System.currentTimeMillis() // remember the start time
    (1..3).asFlow().onEach { delay(100) } // a number every 100 ms
        .flatMapLatest { hRequestFlow(it) }
        .collect { value -> // collect and print
            Timber.d("$value at ${System.currentTimeMillis() - startTime} ms from start")
        }
}
