package com.cbi.app.trs.domain.usecases.mobility

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.MobilityStatus
import com.cbi.app.trs.domain.repositories.MobilityRepository
import javax.inject.Inject

class GetMobilityStatus
@Inject constructor(private val mobilityRepository: MobilityRepository) : UseCase<MobilityStatus, Int?>() {

    override suspend fun run(userId: Int?) = mobilityRepository.getMobilityStatus(userId)
}