package com.cbi.app.trs.domain.usecases.movie

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.upsell.UpsellEntity
import com.cbi.app.trs.domain.repositories.MovieRepository
import com.cbi.app.trs.domain.usecases.PagingParam
import javax.inject.Inject

class PostUpSell
@Inject constructor(private val movieRepository: MovieRepository) : UseCase<UpsellEntity.Data, Pair<Int, PostUpSell.Params>>() {
    data class Params(val reference_video_id: Int?) : PagingParam()

    override suspend fun run(params: Pair<Int, Params>): Either<Failure, UpsellEntity.Data> {
        val queries = params.second
        return movieRepository.getUpSell(params.first, queries.reference_video_id, queries.limit, queries.page)
    }

}