/*
 * Copyright (c) 2021/  3/ 14.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow.channels

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import timber.log.Timber

/*
*Coroutines are the preferred way to build non-blocking, concurrent applications
* in Kotlin. Channels allow coroutines to communicate with each other.
*
*
* A Channel is conceptually very similar to BlockingQueue. One key difference
* is that instead of a blocking put operation it has a suspending send,
* and instead of a blocking take operation it has a suspending receive.
*  */





fun hRunBasicChannel() = GlobalScope.launch {
    val channel = Channel<Int>()
    launch {
        // this might be heavy CPU-consuming computation or async logic
        for (x in 1..5)
            channel.send(x * x)
    }
// here we print five received integers:
    repeat(5) {
        Timber.d(" Channel Recieved ${channel.receive()}")
    }
    Timber.d("Done!")
}


//////////////////////////////////////////////////////////////////////////////////////////////
/*
* Closing and iteration over channels

Unlike a queue, a channel can be closed to indicate that no more elements are coming.
* On the receiver side it is convenient to use a regular for loop to receive elements
*  from the channel.
*  A close is like sending a special close token to the channel. The iteration
* stops as soon as this close token is received, so there is a guarantee that all previously
* sent elements before the close are received:
* */

fun hRunChannelWithClosed() = GlobalScope.launch {
    val channel = Channel<Int>()
    launch {
        for (x in 1..5) channel.send(x * x)
        channel.close() // we're done sending
    }
// here we print received values using `for` loop (until the channel is closed)
    for (y in channel) Timber.d("Channel Recieving$y")
    Timber.d("Done!")
}


/*
* Building channel producers﻿
* The pattern where a coroutine is producing a sequence of elements is quite common.
* This is a part of producer-consumer pattern that is often found in concurrent code.
* Such a producer can be abstracted out into a function that takes channel as its parameter,
* There is a convenient coroutine builder named produce that makes it easy to do it right
* on producer side, and an extension function consumeEach, that replaces a for loop on the
consumer side:
 */

fun CoroutineScope.produceSquares(): ReceiveChannel<Int> = produce {
    for (x in 1..5) send(x * x)
}

fun hRunChannelWithProducer() = GlobalScope.launch {
    val squares = produceSquares()
    squares.consumeEach { Timber.d("Consume Each $it") }
    Timber.d("Done!")
}