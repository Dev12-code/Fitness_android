package com.cbi.app.trs.features.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.cbi.app.trs.core.platform.BaseActivity
import com.cbi.app.trs.features.fragments.login.LoginFragment

class LoginActivity : BaseActivity() {
    companion object {
//        const val HAS_EXPIRED = "HAS_EXPIRED"
        fun callingIntent(context: Context) = Intent(context, LoginActivity::class.java)
        fun callingIntentForExpired(context: Context) = Intent(context, LoginActivity::class.java)/*.apply { putExtra(HAS_EXPIRED, true) }*/
    }

    override fun fragment() = LoginFragment().apply { arguments = intent.extras }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeFullScreenMode()
    }
}
