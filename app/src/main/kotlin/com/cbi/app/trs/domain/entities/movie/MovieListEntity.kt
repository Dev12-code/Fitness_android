package com.cbi.app.trs.domain.entities.movie

import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.BaseEntities

data class MovieListEntity(val data: List<MovieData>) : BaseEntities() {
    companion object {
        fun empty() = MovieListEntity(ArrayList())
    }
}