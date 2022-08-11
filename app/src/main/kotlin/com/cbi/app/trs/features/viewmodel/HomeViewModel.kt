package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.MobilityStatus
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.domain.entities.upsell.UpsellEntity
import com.cbi.app.trs.domain.usecases.mobility.GetMobilityStatus
import com.cbi.app.trs.domain.usecases.movie.GetFeatureMovie
import com.cbi.app.trs.domain.usecases.movie.PostUpSell
import com.cbi.app.trs.domain.usecases.system.GetSystemBonus
import javax.inject.Inject

class HomeViewModel @Inject constructor(
        private val getMobilityStatus: GetMobilityStatus,
        private val getSystemBonus: GetSystemBonus,
        private val postUpSell: PostUpSell,
        private val getFeatureMovie: GetFeatureMovie
) : BaseViewModel() {
    var mobilityStatus: MutableLiveData<MobilityStatus> = MutableLiveData()
    var systemBonus: MutableLiveData<List<SystemData.Bonus>> = MutableLiveData()
    var upSell: MutableLiveData<UpsellEntity.Data> = MutableLiveData()
    var featureMovie: MutableLiveData<List<MovieData>> = MutableLiveData()

    fun loadSystemBonus() = getSystemBonus(true) { it.fold(::handleFailure, ::handleSystemBonus) }
    fun getMobilityStatus(userID: Int?) = getMobilityStatus(userID) { it.fold(::handleFailure, ::handleMobilityStatus) }
    fun getUpSell(pair: Pair<Int, PostUpSell.Params>) = postUpSell(pair) { it.fold(::handleFailure, ::handleUpsell) }
    fun getFeatureMovie(param: Pair<Int, GetFeatureMovie.Params>) = getFeatureMovie(param) { it.fold(::handleFailure, ::handleFeatureMovie) }

    private fun handleMobilityStatus(mobilityStatus: MobilityStatus) {
        this.mobilityStatus.value = mobilityStatus
    }

    private fun handleSystemBonus(systemBonus: List<SystemData.Bonus>) {
        this.systemBonus.value = systemBonus
    }

    private fun handleUpsell(data: UpsellEntity.Data) {
        this.upSell.value = data
    }

    private fun handleFeatureMovie(list: List<MovieData>) {
        this.featureMovie.value = list
    }
}