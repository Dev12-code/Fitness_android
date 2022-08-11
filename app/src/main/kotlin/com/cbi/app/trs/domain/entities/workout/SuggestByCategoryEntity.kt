package com.cbi.app.trs.domain.entities.workout

import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.BaseEntities

data class SuggestByCategoryEntity(val data: Data) : BaseEntities() {
    data class Data(val total: Int, val max_page: Int, val page: Int, val limit: Int, val suggestion_videos: ArrayList<MovieData>)
    companion object {
        fun empty() = SuggestByCategoryEntity(Data(0, 0, 0, 0, ArrayList<MovieData>()))
    }
}