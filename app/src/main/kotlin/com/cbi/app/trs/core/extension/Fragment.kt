package com.cbi.app.trs.core.extension

import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.ViewModelProviders
import com.cbi.app.trs.core.platform.BaseActivity
import com.cbi.app.trs.core.platform.BaseFragment

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) =
    beginTransaction().func().commit()

inline fun <reified T : ViewModel> Fragment.viewModel(factory: Factory, body: T.() -> Unit): T {
    val vm = ViewModelProviders.of(this, factory)[T::class.java]
    vm.body()
    return vm
}

fun BaseFragment.close() {
    fragmentManager?.let {
        if (!it.isStateSaved) {
            it.popBackStackImmediate()
        }
    }
}

val BaseFragment.appContext: Context get() = activity?.applicationContext!!