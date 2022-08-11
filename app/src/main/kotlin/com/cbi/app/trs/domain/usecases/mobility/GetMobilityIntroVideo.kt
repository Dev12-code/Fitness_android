package com.cbi.app.trs.domain.usecases.mobility

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.repositories.MobilityRepository
import javax.inject.Inject


class GetMobilityIntroVideo @Inject constructor(private val mobilityRepository: MobilityRepository): UseCase<MovieData, Int?>(){
    override suspend fun run(userId: Int?) = mobilityRepository.getMobilityIntroVideo(userId)

}