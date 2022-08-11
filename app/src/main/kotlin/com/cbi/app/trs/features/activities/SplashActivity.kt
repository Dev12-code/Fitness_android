package com.cbi.app.trs.features.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.cbi.app.trs.core.platform.BaseActivity
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.features.fragments.splash.SplashFragment

class SplashActivity : BaseActivity() {
    companion object {
        fun callingIntent(context: Context) = Intent(context, SplashActivity::class.java)
    }

    override fun fragment(): BaseFragment? = SplashFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeFullScreenMode()
    }
}
