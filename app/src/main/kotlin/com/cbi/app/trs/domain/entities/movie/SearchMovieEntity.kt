package com.cbi.app.trs.domain.entities.movie

import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.BaseEntities

data class SearchMovieEntity(val data: Data) : BaseEntities() {
    data class Data(val total: Int, val max_page: Int, val page: Int, val limit: Int, val list_video: List<MovieData>)
    companion object {
        fun empty() = SearchMovieEntity(Data(0, 0, 0, 0, ArrayList<MovieData>()))
    }
}