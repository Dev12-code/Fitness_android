package com.cbi.app.trs.domain.usecases.mobility

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.repositories.MobilityRepository
import javax.inject.Inject

class UpdateMobilityResult
@Inject constructor(private val mobilityRepository: MobilityRepository) : UseCase<BaseEntities, Pair<Int?, UpdateMobilityResult.Param>>() {

    data class Param(val shoulder_point_avg: Double, val trunk_point_avg: Double,
                     val hip_point_avg: Double, val ankle_point_avg: Double, val on_process: Boolean)

    override suspend fun run(pair: Pair<Int?, UpdateMobilityResult.Param>) = mobilityRepository.updateMobilityStatus(pair.first, pair.second)
}