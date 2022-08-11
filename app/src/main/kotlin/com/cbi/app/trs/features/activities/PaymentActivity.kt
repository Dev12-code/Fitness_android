package com.cbi.app.trs.features.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.cbi.app.trs.core.platform.BaseActivity
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.features.fragments.payment.PaymentBaseFragment
import com.cbi.app.trs.features.fragments.payment.free_trial.FreeTrialFragment
import com.cbi.app.trs.features.fragments.payment.price.PriceFragment
import com.cbi.app.trs.features.fragments.search.SearchFragment

class PaymentActivity : BaseActivity() {
    companion object {
        const val PRICE_FRAGMENT = "PRICE_FRAGMENT"

        const val TYPE = "TYPE"

        fun callingIntent(context: Context) = Intent(context, PaymentActivity::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changeFullScreenMode()
    }

    override fun fragment(): BaseFragment? {
        val type = intent.getStringExtra(TYPE)
        if (type == PRICE_FRAGMENT) {
            return PriceFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(PriceFragment.ALLOW_TO_FINISH_VIEW, true)
                }
            }
        }
        return FreeTrialFragment()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (getCurrentFragment() is PaymentBaseFragment) {
            (getCurrentFragment() as PaymentBaseFragment).onActivityResult(requestCode, resultCode, data)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}
