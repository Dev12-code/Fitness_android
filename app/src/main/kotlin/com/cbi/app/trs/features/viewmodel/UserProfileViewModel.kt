package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.usecases.system.GetSystemArea
import com.cbi.app.trs.domain.usecases.system.GetSystemEquipment
import com.cbi.app.trs.domain.usecases.user.GetUserProfile
import com.cbi.app.trs.domain.usecases.user.PostUserAvatar
import com.cbi.app.trs.domain.usecases.user.PostUserProfile
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class UserProfileViewModel
@Inject constructor(private val getUserProfile: GetUserProfile,
                    private val postUserProfile: PostUserProfile,
                    private val getSystemArea: GetSystemArea,
                    private val getSystemEquipment: GetSystemEquipment,
                    private val postUserAvatar: PostUserAvatar) : BaseViewModel() {
    var userProfile: MutableLiveData<UserData.UserProfile> = MutableLiveData()
    var updateUserProfile: MutableLiveData<BaseEntities> = MutableLiveData()
    var systemArea: MutableLiveData<List<SystemData.Area>> = MutableLiveData()
    var systemEquipment: MutableLiveData<List<SystemData.Equipment>> = MutableLiveData()

    fun loadSystemArea() = getSystemArea(true) { it.fold(::handleFailure, ::handleSystemArea) }
    fun loadSystemEquipment() = getSystemEquipment(false) { it.fold(::handleFailure, ::handleSystemEquipment) }


    private fun handleSystemArea(systemArea: List<SystemData.Area>) {
        this.systemArea.value = systemArea
    }

    private fun handleSystemEquipment(systemEquipment: List<SystemData.Equipment>) {
        this.systemEquipment.value = systemEquipment
    }

    fun getUserProfile(userID: Int) = getUserProfile(userID) { it.fold(::handleFailure, ::handleUserProfile) }

    private fun handleUserProfile(userProfile: UserData.UserProfile) {
        this.userProfile.value = userProfile
    }

    fun updateUserProfile(pair: Pair<Int, PostUserProfile.Param>) = postUserProfile(pair) { it.fold(::handleFailure, ::handleUserProfileUpdate) }

    private fun handleUserProfileUpdate(baseEntities: BaseEntities) {
        this.updateUserProfile.value = baseEntities
    }

    fun updateUserAvatar(pair: Pair<Int, MultipartBody.Part>) = postUserAvatar(pair) { it.fold(::handleFailure, ::handleUserProfileUpdate) }

}