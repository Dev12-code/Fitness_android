package com.cbi.app.trs.domain.usecases.activity

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.repositories.ActivityRepository
import com.cbi.app.trs.domain.usecases.PagingParam
import javax.inject.Inject

class GetHistory @Inject constructor(private val activityRepository: ActivityRepository) : UseCase<SearchMovieEntity.Data, Pair<Int?, PagingParam>>() {
    override suspend fun run(params: Pair<Int?, PagingParam>): Either<Failure, SearchMovieEntity.Data> {
        val queries = params.second
        return activityRepository.getHistory(params.first, queries.limit, queries.page)
    }
}