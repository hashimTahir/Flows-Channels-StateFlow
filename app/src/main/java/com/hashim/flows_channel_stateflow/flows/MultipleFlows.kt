/*
 * Copyright (c) 2021/  3/ 13.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow.flows

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/*
* Composing multiple flows﻿
* There are lots of ways to compose multiple flows.
* Like the Sequence.zip extension function in the Kotlin standard library,
*  flows have a zip operator that combines the corresponding values of two flows:
* Same as zip is used to combine multiple observables in RxJava.
* */



@InternalCoroutinesApi
fun hRunnerForZipFlow() = runBlocking {
    val hNumbersFlow = (1..3).asFlow()
    val hStringFlow = flowOf("one", "two", "three")
    hNumbersFlow.zip(hStringFlow) { number, string -> "$number -> $string" } // compose a single string
        .collect {
            Timber.d("Zip Flow $it")
        }
}


//////////////////////////////////////////////////////////////////////////////////////////////////


/*
* Combine﻿
* When flow represents the most recent value of a variable or operation,
*  it might be needed to perform a computation that depends on the most recent
*  values of the corresponding flows and to recompute it whenever any of the upstream
*  flows emit a value. The corresponding family of operators is called combine.
*
*
* Eg. if the numbers in the previous example update every 300ms, but strings update every 400 ms,
*  then zipping them using the zip operator will still produce the same result, although
*  results are printed every 400 ms:
*
*
* Prints with zip
* Combine Flow 1 -> one at 495 ms From start
* Combine Flow 2 -> two at 895 ms From start
* Combine Flow 3 -> three at 1308 ms From start
*
*
*
*
* prints with Combine
*
* Combine Flow 1 -> one at 922 ms From start
*  Combine Flow 2 -> one at 1115 ms From start
* Combine Flow 2 -> two at 1326 ms From start
* Combine Flow 3 -> two at 1419 ms From start
*  Combine Flow 3 -> three at 1731 ms From start
*
* anytime the value is changed the computation is done again.
*
* */


fun hRunnerForCombineFlow() = runBlocking {

//     onEach intermediate operator is used delay each element by 300ms
    val hNumbersFlow = (1..3).asFlow().onEach {
        delay(300)
    }

    val hStringFlow = flowOf("one", "two", "three").onEach {
        delay(400)
    }

    val hStartTime = System.currentTimeMillis()

    hNumbersFlow.combine(hStringFlow) { number, string -> "$number -> $string" } // compose a single string
        .collect {
            Timber.d("Combine Flow $it at ${System.currentTimeMillis() - hStartTime} ms From start")
        }
}
