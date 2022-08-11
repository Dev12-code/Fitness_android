package com.cbi.app.trs.domain.usecases.movie

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.movie.ReferenceMovieEntity
import com.cbi.app.trs.domain.repositories.MovieRepository
import javax.inject.Inject

class GetReferenceMovie @Inject constructor(private val movieRepository: MovieRepository) : UseCase<ReferenceMovieEntity, Pair<Int, GetReferenceMovie.Params>>() {
    data class Params(val video_id: Int)

    override suspend fun run(params: Pair<Int, Params>): Either<Failure, ReferenceMovieEntity> {
        val queries = params.second
        return movieRepository.getReferenceMovie(params.first, queries.video_id)
    }


}