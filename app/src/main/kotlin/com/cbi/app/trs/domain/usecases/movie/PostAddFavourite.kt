package com.cbi.app.trs.domain.usecases.movie

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.repositories.MovieRepository
import javax.inject.Inject

class PostAddFavourite @Inject constructor(private val movieRepository: MovieRepository) : UseCase<BaseEntities, Pair<Int, PostAddFavourite.Params>>() {
    data class Params(val video_id: Int)

    override suspend fun run(param: Pair<Int, Params>) = movieRepository.addFavourite(param.first, param.second)
}