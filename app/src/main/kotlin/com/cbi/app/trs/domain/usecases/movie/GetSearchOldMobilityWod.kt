package com.cbi.app.trs.domain.usecases.movie

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.repositories.MovieRepository
import javax.inject.Inject

class GetSearchOldMobilityWod @Inject constructor(private val movieRepository: MovieRepository) : UseCase<SearchMovieEntity.Data, Pair<Int, GetSearchResult.Params>>() {

    override suspend fun run(params: Pair<Int, GetSearchResult.Params>): Either<Failure, SearchMovieEntity.Data> {
        val queries = params.second
        return movieRepository.getSearchOldMobilityResult(params.first, queries.search_keyword, queries.equipment_ids, queries.collection, queries.focus_areas, queries.min_duration, queries.max_duration, queries.limit, queries.page)
    }

}