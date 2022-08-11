package com.cbi.app.trs.features.utils

import android.util.Log
import com.cbi.app.trs.BuildConfig

class AppLog {
    companion object {
        @JvmStatic
        fun e(tag: String, message: String) {
            if (BuildConfig.DEBUG) Log.e(tag, message)
        }

        fun e(tag: String, message: String, s: Throwable) {
            if (BuildConfig.DEBUG) Log.e(tag, message, s)
        }

        fun d(tag: String, message: String) {
            if (BuildConfig.DEBUG) Log.d(tag, message)
        }
    }
}