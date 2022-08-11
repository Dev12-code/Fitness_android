package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.usecases.pain.GetMobilityRxPain
import com.cbi.app.trs.domain.usecases.pain.GetStartedPain
import com.cbi.app.trs.domain.usecases.pain.GetUnderstandingPain
import com.cbi.app.trs.domain.usecases.system.GetSystemPainArea
import com.cbi.app.trs.domain.usecases.user.GetUserProfile
import javax.inject.Inject

class PainViewModel @Inject constructor(private val getSystemPainArea: GetSystemPainArea,
                                        private val getStartedPain: GetStartedPain,
                                        private val getUnderstandingPain: GetUnderstandingPain,
                                        private val getMobilityRxPain: GetMobilityRxPain,
                                        private val getUserProfile: GetUserProfile) : BaseViewModel() {
    var systemPainArea: MutableLiveData<List<SystemData.PainArea>> = MutableLiveData()
    var startedPain: MutableLiveData<MovieData> = MutableLiveData()
    var userProfileData: MutableLiveData<UserData.UserProfile> = MutableLiveData()
    var understandingPain: MutableLiveData<SearchMovieEntity.Data> = MutableLiveData()
    var advancePain: MutableLiveData<SearchMovieEntity.Data> = MutableLiveData()
    var mobilityRxPain: MutableLiveData<SearchMovieEntity.Data> = MutableLiveData()

    fun getSystemPainArea() = getSystemPainArea(true) { it.fold(::handleFailure, ::handleSystemPainArea) }

    private fun handleSystemPainArea(list: List<SystemData.PainArea>) {
        this.systemPainArea.value = list
    }

    fun getStartedPain(pair: Pair<Int?, GetStartedPain.Params>) = getStartedPain(pair) { it.fold(::handleFailure, ::handleStartedPain) }

    private fun handleStartedPain(data: MovieData) {
        this.startedPain.value = data
    }

    fun getUnderstandingPain(pair: Pair<Int?, GetUnderstandingPain.Params>) = getUnderstandingPain(pair) { it.fold(::handleFailure, ::handleUnderstandingPain) }

    private fun handleUnderstandingPain(data: SearchMovieEntity.Data) {
        this.understandingPain.value = data
    }

    fun getMobilityRxPain(pair: Pair<Int?, GetUnderstandingPain.Params>) = getMobilityRxPain(pair) { it.fold(::handleFailure, ::handleMobilityRxPain) }

    private fun handleMobilityRxPain(data: SearchMovieEntity.Data) {
        this.mobilityRxPain.value = data
    }

    fun getAdvancePain(pair: Pair<Int?, GetUnderstandingPain.Params>) = getUnderstandingPain(pair) { it.fold(::handleFailure, ::handleAdvancePain) }

    private fun handleAdvancePain(data: SearchMovieEntity.Data) {
        this.advancePain.value = data
    }

    fun getUserProfile(userID: Int) = getUserProfile(userID) { it.fold(::handleFailure, ::handleUserProfile) }

    private fun handleUserProfile(userProfile: UserData.UserProfile) {
        this.userProfileData.value = userProfile
    }
}