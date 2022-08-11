package com.cbi.app.trs.domain.usecases.movie

import android.os.Parcelable
import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.movie.SearchMovieEntity
import com.cbi.app.trs.domain.repositories.MovieRepository
import com.cbi.app.trs.domain.usecases.PagingParam
import kotlinx.android.parcel.Parcelize
import javax.inject.Inject

class GetSearchResult @Inject constructor(private val movieRepository: MovieRepository) : UseCase<SearchMovieEntity.Data, Pair<Int, GetSearchResult.Params>>() {

    @Parcelize
    data class Params(val search_keyword: String?, val collection: String?, val equipment_ids: List<Int>?,
                      val focus_areas: List<Int>?, val min_duration: Int?, val max_duration: Int?) : Parcelable, PagingParam()

    override suspend fun run(params: Pair<Int, Params>): Either<Failure, SearchMovieEntity.Data> {
        val queries = params.second
       return movieRepository.getSearchResult(params.first, queries.search_keyword, queries.equipment_ids, queries.collection, queries.focus_areas, queries.min_duration, queries.max_duration, queries.limit, queries.page)
    }
}