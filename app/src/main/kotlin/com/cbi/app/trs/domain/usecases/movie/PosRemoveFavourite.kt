package com.cbi.app.trs.domain.usecases.movie

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.repositories.MovieRepository
import javax.inject.Inject

class PosRemoveFavourite @Inject constructor(private val movieRepository: MovieRepository) : UseCase<BaseEntities, Pair<Int, Int>>() {

    override suspend fun run(param: Pair<Int, Int>) = movieRepository.removeFavourite(param.first, param.second)
}