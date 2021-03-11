/*
 * Copyright (c) 2021/  3/ 10.  Created by Hashim Tahir
 */

package com.hashim.flows_channel_stateflow

import android.app.Application
import com.hashim.flows_channel_stateflow.utis.Constants
import timber.log.Timber

/*
 * Copyright (c) 2021/  3/ 10.  Created by Hashim Tahir
 */

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        hInitTimber()

    }

    private fun hInitTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(object : Timber.DebugTree() {
                override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                    super.log(priority, String.format(Constants.hTag, tag), message, t)
                }
            })
        }
    }
}