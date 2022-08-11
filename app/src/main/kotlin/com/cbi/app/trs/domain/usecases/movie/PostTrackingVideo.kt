package com.cbi.app.trs.domain.usecases.movie

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.repositories.MovieRepository
import javax.inject.Inject

class PostTrackingVideo @Inject constructor(private val movieRepository: MovieRepository) : UseCase<BaseEntities, Pair<Int?, PostTrackingVideo.Params>>() {
    data class Params(val video_id: Int, val watched_percent: Double)

    override suspend fun run(param: Pair<Int?, Params>) = movieRepository.trackingVideo(param.first, param.second)
}