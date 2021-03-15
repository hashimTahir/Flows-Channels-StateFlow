/*
 * Copyright (c) 2021/  3/ 15.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow.channels

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ticker
import timber.log.Timber


/*
* Unbuffered channels transfer elements
* when sender and receiver meet each other (aka rendezvous). If send is invoked first,
* then it is suspended until receive is invoked, if receive is invoked first, it is
*  suspended until send is invoked.
* Both Channel() factory function and produce builder take an optional capacity
* parameter to specify buffer size. Buffer allows senders to send multiple elements before
* suspending, similar to the BlockingQueue with a specified capacity, which blocks
* when buffer is full.
* */

/*
* The first four elements are added to the buffer and the sender suspends when trying
*  to send the fifth one.
* */

fun hRunnerForBufferedChannel() = runBlocking {

    val channel = Channel<Int>(4) // create buffered channel
    val sender = launch { // launch sender coroutine
        repeat(10) {
            Timber.d("Sending $it") // print before sending each element
            channel.send(it) // will suspend when buffer is full
        }
    }
    // don't receive anything... just wait....
    delay(1000)
    sender.cancel() // cancel sender coroutine

}

////////////////////////////////////////////////////////////////////////////////////////////////

/*
* Channels are fair
*  Send and receive operations to channels are fair with respect to the order of their
*  invocation from multiple coroutines. They are served in first-in first-out order, e.g.
*  the first coroutine to invoke receive gets the element. In the following example
* two coroutines "ping" and "pong" are receiving the "ball" object from the shared "table" channel.
* */

data class Ball(var hits: Int)

fun hRunnerForFarityChannel() = runBlocking {
    val hTableChannel = Channel<Ball>() // a shared table
    launch { hPlayerReciever("ping", hTableChannel) }
    launch { hPlayerReciever("pong", hTableChannel) }
    hTableChannel.send(Ball(0)) // serve the ball
    delay(1000) // delay 1 second
    coroutineContext.cancelChildren() // game over, cancel them
}

suspend fun hPlayerReciever(name: String, table: Channel<Ball>) {
    for (ball in table) { // receive the ball in a loop
        ball.hits++
        Timber.d("$name $ball")
        delay(300) // wait a bit
        table.send(ball) // send the ball back
    }
}
///////////////////////////////////////////////////////////////////////////////////////////////


/*
* Ticker channels
*
* Ticker channel is a special rendezvous channel that produces Unit every time given
* delay passes since last consumption from this channel. Though it may seem to be
* useless standalone, it is a useful building block to create complex time-based
* produce pipelines and operators that do windowing and other time-dependent
* processing. Ticker channel can be used in select to perform "on tick" action.
* */



fun hRunnerForTickerChannel() = runBlocking<Unit> {
    val tickerChannel = ticker(delayMillis = 100, initialDelayMillis = 0) // create ticker channel
    var nextElement = withTimeoutOrNull(1) { tickerChannel.receive() }
    Timber.d("Initial element is available immediately: $nextElement") // no initial delay

    nextElement =
        withTimeoutOrNull(50) { tickerChannel.receive() } // all subsequent elements have 100ms delay
    Timber.d("Next element is not ready in 50 ms: $nextElement")

    nextElement = withTimeoutOrNull(60) { tickerChannel.receive() }
    Timber.d("Next element is ready in 100 ms: $nextElement")

    // Emulate large consumption delays
    Timber.d("Consumer pauses for 150ms")
    delay(150)
    // Next element is available immediately
    nextElement = withTimeoutOrNull(1) { tickerChannel.receive() }
    Timber.d("Next element is available immediately after large consumer delay: $nextElement")
    // Note that the pause between `receive` calls is taken into account and next element arrives faster
    nextElement = withTimeoutOrNull(60) { tickerChannel.receive() }
    Timber.d("Next element is ready in 50ms after consumer pause in 150ms: $nextElement")

    tickerChannel.cancel() // indicate that no more elements are needed
}