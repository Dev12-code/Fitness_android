package com.cbi.app.trs.domain.entities.workout

import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.BaseEntities

data class WorkoutEntity(val data: Data) : BaseEntities() {
    data class Data(val max_page: Int, val page: Int, val limit: Int, val list_video: List<MovieData>)
    companion object {
        fun empty() = WorkoutEntity(Data(0, 0, 0, ArrayList<MovieData>()))
    }
}