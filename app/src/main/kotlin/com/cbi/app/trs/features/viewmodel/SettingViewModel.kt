package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.HtmlData
import com.cbi.app.trs.domain.usecases.user.GetHelp
import com.cbi.app.trs.domain.usecases.user.GetPolicy
import com.cbi.app.trs.domain.usecases.user.GetUserProfile
import javax.inject.Inject

class SettingViewModel @Inject constructor(private val getHelp: GetHelp,
                                           private val getPolicy: GetPolicy,
                                           private val getUserProfile: GetUserProfile) : BaseViewModel() {
    val policyData: MutableLiveData<HtmlData> = MutableLiveData()
    val helpData: MutableLiveData<HtmlData> = MutableLiveData()

    fun getPolicyData(userID: Int?) = getPolicy(userID) { it.fold(::handleFailure, ::handlePolicyData) }

    private fun handlePolicyData(htmlData: HtmlData) {
        this.policyData.value = htmlData
    }

    fun getHelpData(userID: Int?) = getHelp(userID) { it.fold(::handleFailure, ::handleHelpData) }

    private fun handleHelpData(htmlData: HtmlData) {
        this.helpData.value = htmlData
    }
}