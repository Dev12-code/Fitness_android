package com.cbi.app.trs.domain.usecases.activity

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.repositories.ActivityRepository
import javax.inject.Inject

class TrackingUserStreakUseCase @Inject constructor(private val activityRepository: ActivityRepository) : UseCase<BaseEntities, Int?>() {
    override suspend fun run(params: Int?): Either<Failure, BaseEntities> {
        return activityRepository.trackingUserStreak(params)
    }
}