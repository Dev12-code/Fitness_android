package com.cbi.app.trs.domain.usecases.movie

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.upsell.UpsellEntity
import com.cbi.app.trs.domain.repositories.MovieRepository
import com.cbi.app.trs.domain.usecases.PagingParam
import javax.inject.Inject

class UpSellBestSellerUseCase @Inject constructor(private val movieRepository: MovieRepository) : UseCase<UpsellEntity.Data, Pair<Int, PagingParam>>() {

    override suspend fun run(params: Pair<Int, PagingParam>): Either<Failure, UpsellEntity.Data> {
        val queries = params.second
        return movieRepository.getUpSellBestSeller(params.first, queries.limit, queries.page)
    }

}