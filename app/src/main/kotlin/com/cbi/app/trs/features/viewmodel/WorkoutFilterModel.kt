package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.domain.usecases.system.GetSystemArea
import com.cbi.app.trs.domain.usecases.system.GetSystemEquipment
import javax.inject.Inject

class WorkoutFilterModel @Inject constructor(val getSystemArea: GetSystemArea, val getSystemEquipment: GetSystemEquipment) : BaseViewModel() {
    var systemArea: MutableLiveData<List<SystemData.Area>> = MutableLiveData()
    var systemEquipment: MutableLiveData<List<SystemData.Equipment>> = MutableLiveData()

    fun loadSystemArea() = getSystemArea(true) { it.fold(::handleFailure, ::handleSystemArea) }
    fun loadSystemEquipment() = getSystemEquipment(true) { it.fold(::handleFailure, ::handleSystemEquipment) }


    private fun handleSystemArea(systemArea: List<SystemData.Area>) {
        this.systemArea.value = systemArea
    }

    private fun handleSystemEquipment(systemEquipment: List<SystemData.Equipment>) {
        this.systemEquipment.value = systemEquipment
    }
}