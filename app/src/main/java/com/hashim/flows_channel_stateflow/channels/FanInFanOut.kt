/*
 * Copyright (c) 2021/  3/ 15.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow.channels

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.produce
import timber.log.Timber

/*
* Fan-out
* Multiple coroutines may receive from the same channel, distributing work
* between themselves. Eg. Start with a producer coroutine that is
* periodically producing integers (ten numbers per second):
*
* */

fun CoroutineScope.hProduceNumbers() = produce {
    var x = 1 // start from 1
    while (true) {
        send(x++) // produce next
        delay(100) // wait 0.1s
    }
}

/*
* Then there are several processor coroutines. In this example, they just print
*  their id and received number:
* */

fun CoroutineScope.hLaunchProcessor(
    id: Int,
    channel: ReceiveChannel<Int>
) = launch {
    for (msg in channel) {
        Timber.d("Processor #$id received $msg")
    }
}

/*
* launch five processors and let them work for almost a second.
*
* Iterating  over channel with for loop to perform fan-out in launchProcessor code.
* Unlike consumeEach, this for loop pattern is perfectly safe to use from multiple coroutines.
* If one of the processor coroutines fails, then others would still be processing the channel,
* while a processor that is written via consumeEach always consumes (cancels) the underlying
* channel on its normal or abnormal completion.
 */


fun hRunnerForFanOut() = runBlocking {
    val producer = hProduceNumbers()
    repeat(5) {
        Timber.d("Repeat $it and the producer is $producer")
        hLaunchProcessor(it, producer)
    }
    delay(950)
    producer.cancel() // cancel producer coroutine and thus kill them all
}
////////////////////////////////////////////////////////////////////////////////////////

/*
* Fan-in
* Multiple coroutines may send to the same channel. For example, A channel of strings,
*  and a suspending function that repeatedly sends a specified string to this channel with a specified delay:
* */

suspend fun hSendString(
    channel: SendChannel<String>,
    string: String,
    time: Long
) {
    while (true) {
        delay(time)
        channel.send(string)
    }
}


fun hRunnerForFanIn() = runBlocking {
    val channel = Channel<String>()
    launch { hSendString(channel, "foo", 200L) }
    launch { hSendString(channel, "BAR!", 500L) }
    repeat(6) { // receive first six
        Timber.d(channel.receive())
    }
    coroutineContext.cancelChildren() // cancel all children to let main finish
}