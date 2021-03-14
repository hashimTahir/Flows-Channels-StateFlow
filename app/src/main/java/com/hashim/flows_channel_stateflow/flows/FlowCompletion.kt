/*
 * Copyright (c) 2021/  3/ 14.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow.flows

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/*
* Flow completion
* When flow collection completes (normally or exceptionally) it may need to execute
* an action. It can be done in two ways: imperative or declarative.
* */


private fun hEmitFlow(): Flow<Int> = flow {

    for (i in 1..3) {
        delay(100)
        Timber.d("Emitting $i")
        emit(i)
    }
}

fun hRunnerImperativeFlowCompletion() = runBlocking {
    try {
        hEmitFlow().collect {
            Timber.d("Value Collected $it")
        }
    } finally {
        Timber.d("hRunnerImperativeFlowCompletion Done")
    }

}


fun hRunnerDeclarativeFlowCompletion() = runBlocking {

    hEmitFlow()
        .onCompletion {
            Timber.d("hRunnerDeclarativeFlowCompletion Done")

        }
        .collect {
            Timber.d("Value Collected $it")
        }
}
//////////////////////////////////////////////////////////////////////////////////////////////


/*
* The key advantage of onCompletion is a nullable Throwable parameter of the lambda that
*  can be used to determine whether the flow collection was completed normally or
* exceptionally. In the following example the hEmitExceptionFlow  throws an exception
* after emitting the number 1:
*
*
* The onCompletion operator, unlike catch, does not handle the exception.
* The exception still flows downstream. It will be delivered to further
* onCompletion operators and can be handled with a catch operator.
*
* */


private fun hEmitExceptionFlow(): Flow<Int> = flow {

    for (i in 1..3) {
        delay(100)
        Timber.d("Emitting $i")
        emit(i)
        throw RuntimeException()
    }
}


fun hRunnerEmitExceptionFlow() = runBlocking {

    hEmitExceptionFlow()
        .onCompletion { cause ->
            if (cause != null)
                Timber.d("Flow completed exceptionally $cause")
            Timber.d("hRunnerDeclarativeFlowCompletion Done")

        }
        .catch { cause ->
            Timber.d("Caught exception $cause")
        }
        .collect {
            Timber.d("Value Collected $it")
        }
}
///////////////////////////////////////////////////////////////////////////////////


/*
* * Another difference with catch operator is that onCompletion sees all exceptions
*  and receives a null exception only on successful completion of the upstream
*  flow (without cancellation or failure).
* */


fun hRunnerSuccessfulCompletion() = runBlocking<Unit> {
    hEmitFlow()
        .onCompletion { cause ->
            Timber.d("Flow completed with $cause")
        }
        .collect { value ->
            check(value <= 1) { "Checked $value" }
            Timber.d("Collected  value $value")
        }
}