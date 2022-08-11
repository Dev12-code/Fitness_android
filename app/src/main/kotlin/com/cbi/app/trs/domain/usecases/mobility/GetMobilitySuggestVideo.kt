package com.cbi.app.trs.domain.usecases.mobility

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.MobilityStatus
import com.cbi.app.trs.domain.entities.mobility.MobilitySuggestVideoEntity
import com.cbi.app.trs.domain.entities.mobility.MobilityTestVideoEntity
import com.cbi.app.trs.domain.repositories.MobilityRepository
import javax.inject.Inject

class GetMobilitySuggestVideo
@Inject constructor(private val mobilityRepository: MobilityRepository) : UseCase<MobilitySuggestVideoEntity.Data, Int?>() {

    override suspend fun run(userId: Int?) = mobilityRepository.getMobilitySuggestVideo(userId)
}