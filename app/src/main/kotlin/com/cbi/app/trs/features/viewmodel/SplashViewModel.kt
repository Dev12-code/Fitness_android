package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.system.SystemConfigEntity
import com.cbi.app.trs.domain.usecases.system.*
import com.cbi.app.trs.domain.usecases.user.GetUserProfile
import javax.inject.Inject

class SplashViewModel
@Inject constructor(private val getSystemConfig: GetSystemConfig,
                    private val getSystemArea: GetSystemArea,
                    private val getSystemAreaFiltered: GetSystemAreaFiltered,
                    private val getSystemPainArea: GetSystemPainArea,
                    private val getSystemEquipment: GetSystemEquipment,
                    private val getSystemAchievement: GetSystemAchievement,
                    private val getSystemCollection: GetSystemCollection,
                    private val getSystemPreWorkout: GetSystemPreWorkout,
                    private val getSystemPostWorkout: GetSystemPostWorkout,
                    private val getSystemBonus: GetSystemBonus,
                    private val getUserProfile: GetUserProfile) : BaseViewModel() {
    var systemConfig: MutableLiveData<SystemConfigEntity.Data> = MutableLiveData()
    var systemArea: MutableLiveData<List<SystemData.Area>> = MutableLiveData()
    var systemAreaFiltered: MutableLiveData<List<SystemData.Area>> = MutableLiveData()
    var systemPainArea: MutableLiveData<List<SystemData.PainArea>> = MutableLiveData()
    var systemEquipment: MutableLiveData<List<SystemData.Equipment>> = MutableLiveData()
    var systemAchievement: MutableLiveData<List<SystemData.Achievement>> = MutableLiveData()
    var systemCollection: MutableLiveData<List<SystemData.Collection>> = MutableLiveData()
    var systemPreWorkout: MutableLiveData<List<SystemData.PreWorkout>> = MutableLiveData()
    var systemPostWorkout: MutableLiveData<List<SystemData.PostWorkout>> = MutableLiveData()
    var systemBonus: MutableLiveData<List<SystemData.Bonus>> = MutableLiveData()
    var userProfile: MutableLiveData<UserData.UserProfile> = MutableLiveData()

    fun loadSystemConfig() = getSystemConfig(UseCase.None()) { it.fold(::handleFailure, ::handleSystemConfig) }
    fun loadSystemArea(fromCache: Boolean = false) = getSystemArea(fromCache) { it.fold(::handleFailure, ::handleSystemArea) }
    fun loadSystemAreaFiltered(fromCache: Boolean = false) = getSystemAreaFiltered(fromCache) { it.fold(::handleFailure, ::handleSystemAreaFiltered) }
    fun loadSystemPainArea(fromCache: Boolean = false) = getSystemPainArea(fromCache) { it.fold(::handleFailure, ::handleSystemPainArea) }
    fun loadSystemEquipment(fromCache: Boolean = false) = getSystemEquipment(fromCache) { it.fold(::handleFailure, ::handleSystemEquipment) }
    fun loadSystemAchievement(fromCache: Boolean = false) = getSystemAchievement(fromCache) { it.fold(::handleFailure, ::handleSystemAchievement) }
    fun loadSystemCollection(fromCache: Boolean = false) = getSystemCollection(fromCache) { it.fold(::handleFailure, ::handleSystemCollection) }
    fun loadSystemPreWorkout(fromCache: Boolean = false) = getSystemPreWorkout(fromCache) { it.fold(::handleFailure, ::handleSystemPreWorkout) }
    fun loadSystemPostWorkout(fromCache: Boolean = false) = getSystemPostWorkout(fromCache) { it.fold(::handleFailure, ::handleSystemPostWorkout) }
    fun loadSystemBonus(fromCache: Boolean = false) = getSystemBonus(fromCache) { it.fold(::handleFailure, ::handleSystemBonus) }
    fun getUserProfile(userID: Int) = getUserProfile(userID) { it.fold(::handleFailure, ::handleUserProfile) }

    private fun handleSystemPainArea(systemPainArea: List<SystemData.PainArea>) {
        this.systemPainArea.value = systemPainArea
    }

    private fun handleUserProfile(userProfile: UserData.UserProfile) {
        this.userProfile.value = userProfile
    }

    private fun handleSystemArea(systemArea: List<SystemData.Area>) {
        this.systemArea.value = systemArea
    }

    private fun handleSystemAreaFiltered(systemArea: List<SystemData.Area>) {
        this.systemAreaFiltered.value = systemArea
    }

    private fun handleSystemConfig(systemConfig: SystemConfigEntity.Data) {
        this.systemConfig.value = systemConfig
    }

    private fun handleSystemEquipment(data: List<SystemData.Equipment>) {
        this.systemEquipment.value = data
    }

    private fun handleSystemAchievement(data: List<SystemData.Achievement>) {
        this.systemAchievement.value = data
    }

    private fun handleSystemCollection(data: List<SystemData.Collection>) {
        this.systemCollection.value = data
    }

    private fun handleSystemPreWorkout(data: List<SystemData.PreWorkout>) {
        this.systemPreWorkout.value = data
    }

    private fun handleSystemPostWorkout(data: List<SystemData.PostWorkout>) {
        this.systemPostWorkout.value = data
    }

    private fun handleSystemBonus(data: List<SystemData.Bonus>) {
        this.systemBonus.value = data
    }
}