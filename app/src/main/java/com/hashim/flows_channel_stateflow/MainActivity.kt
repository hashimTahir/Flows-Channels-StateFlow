/*
 * Copyright (c) 2021/  3/ 10.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hashim.flows_channel_stateflow.flows.hRunnerForTerminalOperator
import kotlinx.coroutines.InternalCoroutinesApi

@InternalCoroutinesApi
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//        hRunnerForThreadBlockingBlock()
//        hRunnerForNonUiThreadBlocking()
//        hRunnerForFlow()
//        hRunnerForCancellationFlow()
//        hRunnerForCondiationalCancelation()
//        hRunnerForMapIntToString()
//        hRunnerForTransform()
//        hRunnerForLimitOperator()
        hRunnerForTerminalOperator()
    }

}


