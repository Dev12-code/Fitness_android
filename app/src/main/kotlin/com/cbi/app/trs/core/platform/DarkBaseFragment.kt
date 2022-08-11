package com.cbi.app.trs.core.platform

abstract class DarkBaseFragment : BaseFragment() {
    override fun onResume() {
        super.onResume()
        (activity as BaseActivity).changeFullScreenMode()
    }
}