package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.domain.entities.bonus.BonusMovieEntity
import com.cbi.app.trs.domain.usecases.bonus.GetBonusDetail
import com.cbi.app.trs.domain.usecases.system.GetIntro
import com.cbi.app.trs.domain.usecases.system.GetSystemBonus
import javax.inject.Inject

class IntroViewModel @Inject constructor(val getIntro: GetIntro) : BaseViewModel() {
    var introData: MutableLiveData<List<MovieData>> = MutableLiveData()

    fun getIntroMovie() = getIntro(UseCase.None()) { it.fold(::handleFailure, ::handleIntro) }

    private fun handleIntro(list: List<MovieData>) {
        this.introData.value = list
    }
}