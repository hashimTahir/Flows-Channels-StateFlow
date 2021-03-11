/*
 * Copyright (c) 2021/  3/ 10.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        main()
        main()
        main1()
        main2()


    }

    /*
    * This computation blocks the main thread that is running the code.
    * */

    fun ThreadBlockingBlock(): Sequence<Int> = sequence {
        for (i in 1..3) {
            Thread.sleep(1000)
            yield(i)
        }
    }

    fun main() {
        ThreadBlockingBlock().forEach { value -> Timber.d("Value is $value") }
    }


    suspend fun NonUiThreadBlocking(): List<Int> {
        delay(1000) // pretend we are doing something asynchronous here
        return listOf(1, 2, 3)
    }

    fun main1() = runBlocking<Unit> {
        NonUiThreadBlocking().forEach { value -> Timber.d("Value is $value") }
    }


    /*
    * Using the List<Int> result type, means we can only return all the values at once.
    *  To represent the stream of values that are being asynchronously computed,
    * we can use a Flow<Int> type just like we would use the Sequence<Int>
    type for synchronously computed values:
    * This code waits 2000ms before printing each number without blocking the main thread.
    * */


    /*
    *     A builder function for Flow type is called flow.

    Code inside the flow { ... } builder block can suspend.

    The simple function is no longer marked with suspend modifier.

    Values are emitted from the flow using emit function.

    Values are collected from the flow using collect function.
    *
    * Flows are cold streams similar to sequences — the code inside a flow builder
    * does not run until the flow is collected.
    *
    *
    *
    * This is a key reason the simple function (which returns a flow) is not marked with suspend modifier.
    *  By itself, simple() call returns quickly and does not wait for anything.
    * The flow starts every time it is collected, that is why we see "Flow started" when we call collect again.
    *
    * */
    fun simple(): Flow<Int> = flow { // flow builder
        for (i in 1..3) {
            delay(2000) // pretend we are doing something useful here
            emit(i) // emit next value
        }
    }

    fun main2() = runBlocking<Unit> {
        // Launch a concurrent coroutine to check if the main thread is blocked
        launch {
            for (k in 1..2500) {
                Timber.d("Not Blocked  is $k")
                delay(100)
            }
        }
        // Collect the flow
        simple().collect { Timber.d("Value is $it") }
    }
}