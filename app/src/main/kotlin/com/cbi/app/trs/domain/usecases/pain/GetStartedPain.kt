package com.cbi.app.trs.domain.usecases.pain

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.repositories.PainRepository
import javax.inject.Inject

class GetStartedPain @Inject constructor(private val painRepository: PainRepository) : UseCase<MovieData, Pair<Int?, GetStartedPain.Params>>() {
    data class Params(val pain_area_id: Int?)

    override suspend fun run(params: Pair<Int?, Params>): Either<Failure, MovieData> {
        val queries = params.second
        return painRepository.getStartedPain(params.first, queries.pain_area_id)
    }
}