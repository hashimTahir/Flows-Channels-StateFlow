/*
 * Copyright (c) 2021/  3/ 14.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow.flows

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import timber.log.Timber

/*
* Flow collection can complete with an exception when an emitter or code
* inside the operators throw an exception.*/


/*
* Flows must be transparent to exceptions and it is a violation of the exception
* transparency to emit values in the flow builder from inside of a try/catch block.
*  This guarantees that a collector throwing an exception can always catch it
* using try/catch.
*
* The emitter can use a catch operator that preserves this exception
* transparency and allows encapsulation of its exception handling. The body of
* the catch operator can analyze an exception and react to it in different
*  ways depending on which exception was caught:
*
*  Exceptions can be rethrown using throw.
*
* Exceptions can be turned into emission of values using emit from the body of catch.
*
* Exceptions can be ignored, logged, or processed by some other code.
*
*
* */



private fun hCreateExceptionFlow1(): Flow<Int> = flow {
    for (i in 1..3) {
        Timber.d("Emitting $i")
        emit(i) // emit next value
    }
}

@InternalCoroutinesApi
fun hRunnerForFlowException1() = runBlocking {
    try {
        hCreateExceptionFlow1().collect { value ->
            Timber.d("Value Colleced $value")
            /*Check Throws illegal state exception*/
            check(value <= 1) { "Collected yes $value" }
        }
    } catch (e: Throwable) {
        Timber.d("Caught $e")
    }
}
////////////////////////////////////////////////////////////////////////////////////////////


fun hCreateExceptionFlow2(): Flow<String> =
    flow {
        for (i in 1..3) {
            Timber.d("Emitting $i")
            emit(i)
        }
    }
        .map { value ->
            check(value <= 1) { "Crashed on $value" }
            "Value is $value"
        }


fun hRunnerForhCreateExceptionFlow2() = runBlocking<Unit> {
    try {
        hCreateExceptionFlow2()
            .catch { e ->
                emit("CaughtException$e")
            }
            .collect { value -> Timber.d("Collected $value") }
    } catch (e: Throwable) {
        Timber.d("Caught $e")
    }
}


/*
The catch intermediate operator, honoring exception transparency, catches only
upstream exceptions (that is an exception from all the operators above catch,
but not below it). If the block in collect { ... } (placed below catch) throws an exception then
 it escapes:

 A "Caught ..." message is not printed despite there being a catch operator:
 as exception occured downstream.
* */


fun hRunnerForCatchOperator() = runBlocking<Unit> {
    hCreateExceptionFlow1()
        .catch { e ->
            Timber.d("CaughtException$e")
        }
        .collect { value ->
            check(value <= 1) { "Checked $value" }
            Timber.d("Collected $value")
        }
}

////////////////////////////////////////////////////////////////////////////////////////

/*
* Catching declaratively
* The declarative nature of the catch operator can be combined with a desire
* to handle all the exceptions, by moving the body of the collect operator into
*  onEach and putting it before the catch operator.
* Collection of this flow must be triggered by a call to collect() without parameters:
* */



fun hRunnerForDeclarativeException() = runBlocking<Unit> {
    hCreateExceptionFlow1()
        .onEach { value ->
            check(value <= 1) { "Collected $value" }
            Timber.d("Value $value")
        }
        .catch { e -> Timber.d("Caught $e") }
        .collect()
}