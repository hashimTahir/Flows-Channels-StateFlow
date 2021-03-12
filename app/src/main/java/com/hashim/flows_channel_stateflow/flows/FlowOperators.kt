/*
 * Copyright (c) 2021/  3/ 11.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow.flows

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/*
* Flows can be transformed with operators, Intermediate operators are applied to an
* upstream flow and return a downstream flow. These operators are cold, just like flows are.
* A call to such an operator is not a suspending function itself.
* It works quickly, returning the definition of a new transformed flow.
* The basic operators have familiar names like map and filter.
* The important difference to sequences is that blocks of code inside these
* operators can call suspending functions.
* For example, a flow of incoming requests can be mapped to the results
*  with the map operator, even when performing a request is a long-running
*  operation that is implemented by a suspending function:
* */


private suspend fun hMapIntFlowToString(request: Int): String {
    delay(1000) // imitate long-running asynchronous work
    return " response $request"
}

@InternalCoroutinesApi
fun hRunnerForMapIntToString() = runBlocking {
    (1..3).asFlow() // a flow of requests
        .map { request -> hMapIntFlowToString(request) }
        .collect {
            Timber.d(it)
        }
}

///////////////////////////////////////////////////////////////////////////////////
/*
      Transform operator﻿
* using transform a value can be emitted before performing a long-running asynchronous request
* and follow it with a response:
*
* Among the flow transformation operators, the most general one is called transform.
* It can be used to imitate simple transformations like map and filter,
* as well as implement more complex transformations.
* Using the transform operator, an arbitrary values an arbitrary number of times.
* */
private suspend fun hTransformOperator(request: Int): String {
    delay(1000) // imitate long-running asynchronous work
    return "response $request"
}

fun hRunnerForTransform() = runBlocking {
    (1..3).asFlow()
        .transform { request ->
            emit("Making request $request")
            emit(hTransformOperator(request))
        }
        .collect { response ->
            Timber.d(response)
        }
}

///////////////////////////////////////////////////////////////////////////////////







/*
*            Size-limiting operators﻿
* Size-limiting intermediate operators, cancel the execution of the flow when the
* corresponding limit is reached. Cancellation in coroutines is always performed by throwing an
* exception, so that all the resource-management functions (like try { ... } finally { ... } blocks)
* operate normally in case of cancellation.
*/
fun hSizeLimitOperation(): Flow<Int> = flow {
    try {
        emit(1)
        emit(2)
        Timber.d("This line will not execute")
        emit(3)
    } finally {
        Timber.d("Finally in numbers")
    }
}

fun hRunnerForLimitOperator() = runBlocking {
    hSizeLimitOperation().take(2) // take only the first two
        .collect { value ->
            Timber.d("Collecting $value")
        }
}

//////////////////////////////////////////////////////////////////////////////







/*
* Terminal flow operators﻿
* Terminal operators on flows are suspending functions that start a
* collection of the flow. The collect operator is the most basic one.
*
*
* Conversion to various collections like toList and toSet.
*
* Operators to get the first value and to ensure that a flow emits a single value.
*
*
* Reducing a flow to a value with reduce and fold.
* */


fun hRunnerForTerminalOperator() = runBlocking<Unit> {
    val sum = (1..5).asFlow()
        .map { it * it } // squares of numbers from 1 to 5
        .reduce { a, b ->
            Timber.d("Reduce Operator A is $a , B is $b")
            a + b
        } // sum them (terminal operator)
    Timber.d("Sum $sum")
}





