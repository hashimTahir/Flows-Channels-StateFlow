/*
 * Copyright (c) 2021/  3/ 15.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow.channels

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import timber.log.Timber

/*
* Pipeline
* A pipeline is a pattern where one coroutine is producing, possibly infinite, stream of values.
* */


/////////////////////////////////////////////////////////////////////////////////////////////////

fun CoroutineScope.hInfiniteStream(): ReceiveChannel<Int> = produce {
    var x = 1
    while (true) send(x++)
}

/*
* Another coroutine or coroutines are consuming that stream,
* doing some processing, and producing some other results.The numbers from hInfiniteStream
* are squared here.
* */


fun CoroutineScope.hSquare(numbers: ReceiveChannel<Int>)
        : ReceiveChannel<Int> = produce {
    for (x in numbers) send(x * x)
}


fun hRunnerForThePipeLine() = GlobalScope.launch {
    val numbers = hInfiniteStream() // produces integers from 1 and on
    val squares = hSquare(numbers) // squares integers
    repeat(5) {
        Timber.d("Square Recieve ${squares.receive()}") // print first five
    }
    Timber.d("Done!")
    coroutineContext.cancelChildren() // cancel children coroutines

}


//////////////////////////////////////////////////////////////////////////////////////////////

/*
* Pipeline that generates prime numbers using a pipeline of coroutines.
*  start with an infinite sequence of numbers.
* */
fun CoroutineScope.hProduceNumberFrom(start: Int) = produce<Int> {
    var x = start
    while (true) send(x++) // infinite stream of integers from start
}

fun hPrimePipelineRunner() = runBlocking {
    var cur = hProduceNumberFrom(2)
    repeat(10) {
        val prime = cur.receive()
        Timber.d("Recieve Number $prime")
        cur = hFilterPrime(cur, prime)
    }
    coroutineContext.cancelChildren() // cancel all children to let main finish
}


/*
* The following pipeline stage filters an incoming stream of numbers,
* removing all the numbers that are divisible by the given prime number:
* */

fun CoroutineScope.hFilterPrime(numbers: ReceiveChannel<Int>, prime: Int) = produce<Int> {
    for (x in numbers) if (x % prime != 0) send(x)
}