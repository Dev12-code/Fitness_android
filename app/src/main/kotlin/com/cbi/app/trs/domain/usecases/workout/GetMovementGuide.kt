package com.cbi.app.trs.domain.usecases.workout

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.repositories.WorkoutRepository
import javax.inject.Inject

class GetMovementGuide
@Inject constructor(private val workoutRepository: WorkoutRepository) : UseCase<MovieData, Pair<Int?, GetMovementGuide.Param>>() {
    data class Param(var category_id: Int)

    override suspend fun run(params: Pair<Int?, Param>): Either<Failure, MovieData> {
        val queries = params.second
        return workoutRepository.getMovementGuide(params.first, queries.category_id)
    }
}