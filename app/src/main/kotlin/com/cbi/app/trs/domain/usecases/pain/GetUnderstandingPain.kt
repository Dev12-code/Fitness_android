package com.cbi.app.trs.domain.usecases.pain

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.repositories.PainRepository
import com.cbi.app.trs.domain.usecases.PagingParam
import javax.inject.Inject

class GetUnderstandingPain @Inject constructor(private val painRepository: PainRepository) : UseCase<SearchMovieEntity.Data, Pair<Int?, GetUnderstandingPain.Params>>() {
    data class Params(val pain_area_id: Int?, val filter: String? = null) : PagingParam()

    override suspend fun run(params: Pair<Int?, Params>): Either<Failure, SearchMovieEntity.Data> {
        val queries = params.second
        return painRepository.getUnderstandingPain(params.first, queries.pain_area_id, queries.filter, queries.limit, queries.page)
    }
}