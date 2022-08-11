package com.cbi.app.trs.domain.entities.workout

import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.BaseEntities

data class RelatedByCategoryEntity(val data: Data) : BaseEntities() {
    data class Data(val total: Int, val max_page: Int, val page: Int, val limit: Int, val post_related_videos: List<MovieData>)
    companion object {
        fun empty() = RelatedByCategoryEntity(Data(0, 0, 0, 0, ArrayList<MovieData>()))
    }
}