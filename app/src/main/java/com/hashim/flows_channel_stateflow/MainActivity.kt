/*
 * Copyright (c) 2021/  3/ 10.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        main()


    }

    /*
    * This computation blocks the main thread that is running the code.
    * */

    fun simple(): Sequence<Int> = sequence {
        for (i in 1..3) {
            Thread.sleep(1000)
            yield(i)
        }
    }
    fun main() {
        simple().forEach { value -> Timber.d("Value is $value") }
    }
}