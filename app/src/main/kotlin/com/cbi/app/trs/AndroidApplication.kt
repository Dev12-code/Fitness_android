package com.cbi.app.trs

import android.app.Application
import com.bugfender.sdk.Bugfender
import com.cbi.app.trs.core.di.ApplicationComponent
import com.cbi.app.trs.core.di.ApplicationModule
import com.cbi.app.trs.core.di.DaggerApplicationComponent
import com.cbi.app.trs.core.di.ExoModule
import com.cbi.app.trs.data.cache.UserDataCache
import com.cbi.app.trs.features.fragments.login.Authenticator
import javax.inject.Inject

class AndroidApplication : Application() {

    companion object {
        fun isActivityVisible(): Boolean {
            return activityVisible
        }

        fun activityResumed() {
            activityVisible = true
        }

        fun activityPaused() {
            activityVisible = false
        }

        private var activityVisible = false
    }

    @Inject
    lateinit var authenticator: Authenticator

    @Inject
    lateinit var userDataCache: UserDataCache

    val appComponent: ApplicationComponent by lazy(mode = LazyThreadSafetyMode.NONE) {
        DaggerApplicationComponent
                .builder()
                .applicationModule(ApplicationModule(this))
                .exoModule(ExoModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        this.injectMembers()

        //init Bug fender
        Bugfender.init(this, "OWYTwwz2PvU0eN2UJEYJiZp7ffDYsHAV", BuildConfig.DEBUG)
        Bugfender.enableCrashReporting()
        if (authenticator.userLoggedIn()) {
            userDataCache.get()?.user_profile?.email?.let {
                Bugfender.setDeviceString("user_Iden", it)
            }
        }
    }

    private fun injectMembers() = appComponent.inject(this)


}
