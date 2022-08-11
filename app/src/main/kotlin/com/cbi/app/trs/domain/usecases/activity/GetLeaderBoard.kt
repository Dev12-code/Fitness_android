package com.cbi.app.trs.domain.usecases.activity

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.LeaderBoardData
import com.cbi.app.trs.domain.repositories.ActivityRepository
import javax.inject.Inject

class GetLeaderBoard @Inject constructor(private val activityRepository: ActivityRepository) : UseCase<LeaderBoardData, Int?>() {

    override suspend fun run(userID: Int?) = activityRepository.getLeaderBoard(userID)
}