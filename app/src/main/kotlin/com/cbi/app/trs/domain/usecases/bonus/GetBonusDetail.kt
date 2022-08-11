package com.cbi.app.trs.domain.usecases.bonus

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.bonus.BonusMovieEntity
import com.cbi.app.trs.domain.repositories.MovieRepository
import com.cbi.app.trs.domain.usecases.PagingParam
import javax.inject.Inject

class GetBonusDetail @Inject constructor(private val movieRepository: MovieRepository) : UseCase<BonusMovieEntity.Data, Pair<Int?, GetBonusDetail.Params>>() {
    data class Params(val bonus_id: Int?) : PagingParam()

    override suspend fun run(params: Pair<Int?, Params>): Either<Failure, BonusMovieEntity.Data> {
        val queries = params.second
        return movieRepository.getBonusVideoList(params.first, queries.bonus_id, queries.limit, queries.page)
    }

}