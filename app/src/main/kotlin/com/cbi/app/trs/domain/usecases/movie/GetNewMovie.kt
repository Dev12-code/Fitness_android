package com.cbi.app.trs.domain.usecases.movie

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.repositories.MovieRepository
import javax.inject.Inject

class GetNewMovie
@Inject constructor(private val movieRepository: MovieRepository) : UseCase<List<MovieData>, Int>() {

    override suspend fun run(userId: Int) = movieRepository.getNewMovie(userId)
}