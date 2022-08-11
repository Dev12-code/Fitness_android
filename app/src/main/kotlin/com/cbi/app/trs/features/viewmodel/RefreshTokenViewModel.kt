package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.usecases.user.PostRefreshToken
import javax.inject.Inject

class RefreshTokenViewModel
@Inject constructor(
        private val postRefreshToken: PostRefreshToken) : BaseViewModel() {
    var refreshTokenData: MutableLiveData<UserData> = MutableLiveData()
    var failureRefreshData: MutableLiveData<Failure> = MutableLiveData()

    fun refreshToken(refreshToken: String) = postRefreshToken(PostRefreshToken.Params(refreshToken)) { it.fold(::handleFailureRefresh, ::handleRefreshToken) }

    private fun handleRefreshToken(userData: UserData) {
        this.refreshTokenData.value = userData
    }

    private fun handleFailureRefresh(failure: Failure?) {
        this.failureRefreshData.value = failure
    }
}