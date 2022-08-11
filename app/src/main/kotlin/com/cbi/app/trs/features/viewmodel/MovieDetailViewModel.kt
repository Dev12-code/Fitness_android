package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.data.entities.UpsellData
import com.cbi.app.trs.domain.entities.movie.ReferenceMovieEntity
import com.cbi.app.trs.domain.entities.upsell.UpsellEntity
import com.cbi.app.trs.domain.usecases.movie.GetReferenceMovie
import com.cbi.app.trs.domain.usecases.movie.PostUpSell
import com.cbi.app.trs.domain.usecases.system.GetSystemEquipment
import javax.inject.Inject

class MovieDetailViewModel @Inject constructor(val postUpSell: PostUpSell, val getSystemEquipment: GetSystemEquipment,
                                               val getReferenceMovie: GetReferenceMovie) : BaseViewModel() {
    var systemEquipment: MutableLiveData<List<SystemData.Equipment>> = MutableLiveData()
    var referenceMovie: MutableLiveData<ArrayList<MovieData>> = MutableLiveData()
    var upsell: MutableLiveData<UpsellEntity.Data> = MutableLiveData()

    fun getSystemEquipment() = getSystemEquipment(true) { it.fold(::handleFailure, ::handleSystemEquipment) }

    private fun handleSystemEquipment(data: List<SystemData.Equipment>) {
        this.systemEquipment.value = data
    }

    fun getReferenceMovie(pair: Pair<Int, GetReferenceMovie.Params>) = getReferenceMovie(pair) { it.fold(::handleFailure, ::handleReferenceMovie) }

    private fun handleReferenceMovie(referenceMovieEntity: ReferenceMovieEntity) {
        referenceMovie.value = referenceMovieEntity.data

    }

    fun getUpSell(pair: Pair<Int, PostUpSell.Params>) = postUpSell(pair) { it.fold(::handleFailure, ::handleUpsell) }


    private fun handleUpsell(data: UpsellEntity.Data) {
        this.upsell.value = data
    }
}