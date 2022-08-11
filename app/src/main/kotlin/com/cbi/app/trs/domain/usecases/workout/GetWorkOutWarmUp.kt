package com.cbi.app.trs.domain.usecases.workout

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.workout.WorkoutEntity
import com.cbi.app.trs.domain.repositories.WorkoutRepository
import com.cbi.app.trs.domain.usecases.PagingParam
import javax.inject.Inject

class GetWorkOutWarmUp
@Inject constructor(private val workoutRepository: WorkoutRepository) : UseCase<WorkoutEntity.Data, Pair<Int, GetWorkOutWarmUp.Params>>() {
    data class Params(val categoryId: Int) : PagingParam()

    override suspend fun run(params: Pair<Int, Params>): Either<Failure, WorkoutEntity.Data> {
        val queries = params.second
        return workoutRepository.getWarmUp(params.first, queries.categoryId, queries.limit, queries.page)
    }

}