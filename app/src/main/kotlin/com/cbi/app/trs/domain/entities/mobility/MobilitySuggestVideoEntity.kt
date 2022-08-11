package com.cbi.app.trs.domain.entities.mobility

import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.BaseEntities

data class MobilitySuggestVideoEntity(val data: Data = Data()) : BaseEntities() {

    data class Data(val shoulder_videos: List<MovieData> = emptyList(),
                    val trunk_videos: List<MovieData> = emptyList(),
                    val hip_videos: List<MovieData> = emptyList(),
                    val ankle_videos: List<MovieData> = emptyList())
}