package com.cbi.app.trs.domain.usecases.pain

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.repositories.PainRepository
import javax.inject.Inject

class GetMobilityRxPain @Inject constructor(private val painRepository: PainRepository) : UseCase<SearchMovieEntity.Data, Pair<Int?, GetUnderstandingPain.Params>>() {
    override suspend fun run(params: Pair<Int?, GetUnderstandingPain.Params>): Either<Failure, SearchMovieEntity.Data> {
        val queries = params.second
        return painRepository.getMobilityRxPain(params.first, queries.pain_area_id, queries.filter, queries.limit, queries.page)
    }

}