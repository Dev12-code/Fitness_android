package com.cbi.app.trs.features.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import com.cbi.app.trs.AndroidApplication
import com.cbi.app.trs.R
import com.cbi.app.trs.core.di.ApplicationComponent
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.data.cache.UserDataCache
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.fragments.login.Authenticator
import javax.inject.Inject

class NoInternetDialog : DialogAlert() {
    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        (activity?.application as AndroidApplication).appComponent
    }

    @Inject
    lateinit var userDataCache: UserDataCache

    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var authenticator: Authenticator

    var isRetry = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)

        this.setTitle(getString(R.string.something_went_wrong))
        this.setMessage(getString(R.string.failure_network_connection))
        this.setCancel(false)
        if (isRetry) {
            this.setTitlePositive("Retry")
        } else {
            this.setTitlePositive("Setting")
        }
//        this.onPositive { if (isKillApp) activity?.finish() }

        if (authenticator.userLoggedIn()) {
            userDataCache.get()?.user_profile?.let {
                if (!it.isFreeUser()) {
                    this@NoInternetDialog.setTitleNegative("Offline Mode")
                    this@NoInternetDialog.onNegative {
                        if (activity is MainActivity) {
                            (activity as MainActivity).isOfflineMode = true
                            navigator.showDownloaded(activity)
                        } else {
                            navigator.showLanding(activity)
                            activity?.finish()
                        }
                    }
                }
            }
        }
    }
}
