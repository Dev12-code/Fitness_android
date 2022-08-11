package com.cbi.app.trs.core.platform

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics

open class BaseDialog : DialogFragment() {
    private var baseActivity: BaseActivity? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BaseActivity) {
            val mActivity = context as BaseActivity?
            this.baseActivity = mActivity
        }
    }


    override fun onDetach() {
        baseActivity = null
        super.onDetach()
    }

    open fun show(context: Context, tag: String) {
        try {
            if (context is AppCompatActivity) {
                val manager = context.supportFragmentManager
                val transaction = manager.beginTransaction()
                val prevFragment = manager.findFragmentByTag(tag)
                if (prevFragment != null) {
                    transaction.remove(prevFragment)
                }
                transaction.addToBackStack(null)
                super.show(manager, tag)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    open fun showNoDuplicate(context: Context, tag: String) {
        try {
            if (context is AppCompatActivity) {
                val manager = context.supportFragmentManager
                val transaction = manager.beginTransaction()
                val prevFragment = manager.findFragmentByTag(tag)
                if (prevFragment != null) {
                    return
                }
                transaction.addToBackStack(null)
                super.show(manager, tag)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
}