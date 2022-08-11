package com.cbi.app.trs.domain.usecases.movie

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.repositories.MovieRepository
import javax.inject.Inject

class GetMovieDetail @Inject constructor(private val movieRepository: MovieRepository) :
    UseCase<MovieData, Pair<Int?, Int?>>() {

    override suspend fun run(pair: Pair<Int?, Int?>) =
        movieRepository.getMovieDetail(pair.first, pair.second)
}