package com.cbi.app.trs.domain.entities.mobility

import com.cbi.app.trs.data.entities.MobilityTestVideoData
import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.domain.entities.BaseEntities

data class MobilityKellyVideoEntity(val data: MovieData = MovieData.empty()) : BaseEntities()