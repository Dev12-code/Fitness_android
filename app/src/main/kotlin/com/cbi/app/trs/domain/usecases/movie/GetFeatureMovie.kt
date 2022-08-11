package com.cbi.app.trs.domain.usecases.movie

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.repositories.MovieRepository
import com.cbi.app.trs.domain.usecases.PagingParam
import javax.inject.Inject

class GetFeatureMovie
@Inject constructor(private val movieRepository: MovieRepository) :
    UseCase<List<MovieData>, Pair<Int, GetFeatureMovie.Params>>() {
    data class Params(val min_duration: Int, val max_duration: Int, var ro: String? = null) :
        PagingParam()

    override suspend fun run(params: Pair<Int, Params>): Either<Failure, List<MovieData>> {
        val queries = params.second
        return movieRepository.getFeatureMovie(
            params.first,
            queries.min_duration,
            queries.max_duration,
            queries.limit,
            queries.page,
            queries.ro
        )
    }
}