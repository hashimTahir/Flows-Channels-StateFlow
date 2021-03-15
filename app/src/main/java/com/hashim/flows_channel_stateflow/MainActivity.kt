/*
 * Copyright (c) 2021/  3/ 10.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hashim.flows_channel_stateflow.channels.hRunnerForTickerChannel
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//   Note: Use any extenstion functions from any files to test the code.


//        hRunBasicChannel()
//        hRunChannelWithClosed()
//        hRunChannelWithProducer()

//        hRunnerForThePipeLine()
//        hPrimePipelineRunner()
//        hRunnerForFanOut()
//        hRunnerForFanIn()

//        hRunnerForBufferedChannel()
//        hRunnerForFarityChannel()
        hRunnerForTickerChannel()
    }


}


