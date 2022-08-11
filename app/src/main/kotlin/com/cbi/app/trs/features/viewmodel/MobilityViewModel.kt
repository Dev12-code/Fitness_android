package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.MobilityStatus
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.mobility.MobilitySuggestVideoEntity
import com.cbi.app.trs.domain.entities.mobility.MobilityTestVideoEntity
import com.cbi.app.trs.domain.usecases.mobility.*
import javax.inject.Inject

class MobilityViewModel @Inject constructor(private val getMobilityStatus: GetMobilityStatus,
                                            private val getMobilityTestVideo: GetMobilityTestVideo,
                                            private val getMobilitySuggestVideo: GetMobilitySuggestVideo,
                                            private val getMobilityKellyVideo: GetMobilityKellyVideo,
                                            private val getMobilityIntroVideo: GetMobilityIntroVideo,
                                            private val updateMobilityResult: UpdateMobilityResult) : BaseViewModel() {
    var mobilityStatus: MutableLiveData<MobilityStatus> = MutableLiveData()
    var mobilityTestVideo: MutableLiveData<MobilityTestVideoEntity.Data> = MutableLiveData()
    var mobilitySuggestVideo: MutableLiveData<MobilitySuggestVideoEntity.Data> = MutableLiveData()
    var mobilityKellyVideo: MutableLiveData<MovieData> = MutableLiveData()
    var mobilityIntroVideo: MutableLiveData<MovieData> = MutableLiveData()
    var updateResult: MutableLiveData<BaseEntities> = MutableLiveData()

    fun getMobilityStatus(userID: Int?) = getMobilityStatus(userID) { it.fold(::handleFailure, ::handleMobilityStatus) }

    private fun handleMobilityStatus(mobilityStatus: MobilityStatus) {
        this.mobilityStatus.value = mobilityStatus
    }

    fun getMobilityTestVideo(userID: Int?) = getMobilityTestVideo(userID) { it.fold(::handleFailure, ::handleMobilityTestVideo) }

    private fun handleMobilityTestVideo(data: MobilityTestVideoEntity.Data) {
        this.mobilityTestVideo.value = data
    }

    fun getMobilitySuggestVideo(userID: Int?) = getMobilitySuggestVideo(userID) { it.fold(::handleFailure, ::handleMobilitySuggestVideo) }

    private fun handleMobilitySuggestVideo(data: MobilitySuggestVideoEntity.Data) {
        this.mobilitySuggestVideo.value = data
    }

    fun getMobilityKellyVideo(userID: Int?) = getMobilityKellyVideo(userID) { it.fold(::handleFailure, ::handleMobilityKellyVideo) }

    private fun handleMobilityKellyVideo(movieData: MovieData) {
        this.mobilityKellyVideo.value = movieData
    }

    fun getMobilityIntroVideo(userID: Int?) = getMobilityIntroVideo(userID) { it.fold(::handleFailure, ::handleMobilityIntroVideo) }

    private fun handleMobilityIntroVideo(movieData: MovieData) {
        this.mobilityIntroVideo.value = movieData
    }

    fun updateMobilityResult(userID: Int?, param: UpdateMobilityResult.Param) = updateMobilityResult(Pair(userID, param)) { it.fold(::handleFailure, ::handleUpdateMobility) }

    private fun handleUpdateMobility(baseEntities: BaseEntities) {
        this.updateResult.value = baseEntities
    }
}