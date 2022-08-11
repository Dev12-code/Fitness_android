package com.cbi.app.trs.core.platform

import android.content.Context
import com.cbi.app.trs.core.extension.networkInfo
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Injectable class which returns information about the network connection state.
 */
@Singleton
class NetworkHandler
@Inject constructor(private val context: Context) {
    val isConnected get() = if (context.networkInfo != null) context.networkInfo!!.isConnectedOrConnecting else false
}