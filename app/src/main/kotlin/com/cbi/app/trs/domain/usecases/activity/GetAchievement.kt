package com.cbi.app.trs.domain.usecases.activity

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.AchievementData
import com.cbi.app.trs.domain.entities.activity.AchievementEntity
import com.cbi.app.trs.domain.repositories.ActivityRepository
import javax.inject.Inject

class GetAchievement @Inject constructor(private val activityRepository: ActivityRepository) : UseCase<AchievementEntity.Data, Int?>() {

    override suspend fun run(userID: Int?) = activityRepository.getAchievement(userID)
}