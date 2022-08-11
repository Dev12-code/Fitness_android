package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.bonus.BonusMovieEntity
import com.cbi.app.trs.domain.usecases.bonus.GetBonusDetail
import com.cbi.app.trs.domain.usecases.system.GetSystemBonus
import com.cbi.app.trs.domain.usecases.user.GetUserProfile
import javax.inject.Inject

class BonusViewModel @Inject constructor(private val getSystemBonus: GetSystemBonus,
                                         private val getBonusDetail: GetBonusDetail,
                                         private val getUserProfile: GetUserProfile) : BaseViewModel() {
    var systemBonus: MutableLiveData<List<SystemData.Bonus>> = MutableLiveData()
    var bonusDetail: MutableLiveData<BonusMovieEntity.Data> = MutableLiveData()
    var userProfile: MutableLiveData<UserData.UserProfile> = MutableLiveData()

    fun loadSystemBonus() = getSystemBonus(true) { it.fold(::handleFailure, ::handleSystemBonus) }
    fun callBonusDetail(userId: Int?, params: GetBonusDetail.Params) = getBonusDetail(Pair(userId, params)) { it.fold(::handleFailure, ::handleBonusDetail) }
    fun getUserProfile(userID: Int) = getUserProfile(userID) { it.fold(::handleFailure, ::handleUserProfile) }

    private fun handleSystemBonus(systemBonus: List<SystemData.Bonus>) {
        this.systemBonus.value = systemBonus
    }

    private fun handleBonusDetail(data: BonusMovieEntity.Data) {
        this.bonusDetail.value = data
    }

    private fun handleUserProfile(userProfile: UserData.UserProfile) {
        this.userProfile.value = userProfile
    }
}