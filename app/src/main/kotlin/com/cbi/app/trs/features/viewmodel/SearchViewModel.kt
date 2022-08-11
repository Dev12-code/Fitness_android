package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.usecases.movie.*
import com.cbi.app.trs.domain.usecases.system.GetSystemArea
import com.cbi.app.trs.domain.usecases.system.GetSystemAreaFiltered
import javax.inject.Inject

class SearchViewModel @Inject constructor(val getSystemArea: GetSystemArea, val getSearchResult: GetSearchResult,
                                          private val getSystemAreaFiltered: GetSystemAreaFiltered,
                                          val searchDailyResultUseCase: SearchDailyResultUseCase,
                                          val searchOldMobilityWodUseCase: GetSearchOldMobilityWod,
                                          val getFavourite: GetFavourite, val postRemoveFavourite: PosRemoveFavourite) : BaseViewModel() {
    var systemArea: MutableLiveData<List<SystemData.Area>> = MutableLiveData()
    var searchResult: MutableLiveData<SearchMovieEntity.Data> = MutableLiveData()
    var searchOldMobilityWodResult: MutableLiveData<SearchMovieEntity.Data> = MutableLiveData()
    var removeFavouriteResult: MutableLiveData<BaseEntities> = MutableLiveData()
    var searchDailyResult: MutableLiveData<SearchMovieEntity.Data> = MutableLiveData()
    var systemAreaFiltered: MutableLiveData<List<SystemData.Area>> = MutableLiveData()

    fun loadSystemArea() = getSystemArea(true) { it.fold(::handleFailure, ::handleSystemArea) }
    fun search(pair: Pair<Int, GetSearchResult.Params>) = getSearchResult(pair) { it.fold(::handleFailure, ::handleSearchResult) }
    fun searchOldMobility(pair: Pair<Int, GetSearchResult.Params>) = searchOldMobilityWodUseCase(pair) { it.fold(::handleFailure, ::handleSearchOldMobilityResult) }
    fun searchDaily(pair: Pair<Int, GetSearchResult.Params>) = searchDailyResultUseCase(pair) { it.fold(::handleFailure, ::handleSearchDaily) }
    fun favourite(pair: Pair<Int, GetSearchResult.Params>) = getFavourite(pair) { it.fold(::handleFailure, ::handleSearchResult) }
    fun removeFavorite(pair: Pair<Int, Int>) = postRemoveFavourite(pair) { it.fold(::handleFailure, ::handleRemoveFavourite) }
    fun loadSystemAreaFiltered(fromCache: Boolean = false) = getSystemAreaFiltered(fromCache) { it.fold(::handleFailure, ::handleSystemAreaFiltered) }

    private fun handleSystemAreaFiltered(systemArea: List<SystemData.Area>) {
        this.systemAreaFiltered.value = systemArea
    }

    private fun handleRemoveFavourite(data: BaseEntities) {
        this.removeFavouriteResult.value = data
    }

    private fun handleSearchResult(data: SearchMovieEntity.Data) {
        this.searchResult.value = data
    }

    private fun handleSearchOldMobilityResult(data: SearchMovieEntity.Data) {
        this.searchOldMobilityWodResult.value = data
    }

    private fun handleSearchDaily(data: SearchMovieEntity.Data) {
        this.searchDailyResult.value = data
    }

    private fun handleSystemArea(systemArea: List<SystemData.Area>) {
        this.systemArea.value = systemArea
    }
}