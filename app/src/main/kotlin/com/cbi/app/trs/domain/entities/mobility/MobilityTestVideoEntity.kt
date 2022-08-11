package com.cbi.app.trs.domain.entities.mobility

import com.cbi.app.trs.data.entities.MobilityTestVideoData
import com.cbi.app.trs.domain.entities.BaseEntities

data class MobilityTestVideoEntity(val data: Data = Data()) : BaseEntities() {

    data class Data(val shoulder_videos: List<MobilityTestVideoData> = emptyList(),
                    val trunk_videos: List<MobilityTestVideoData> = emptyList(),
                    val hip_videos: List<MobilityTestVideoData> = emptyList(),
                    val ankle_videos: List<MobilityTestVideoData> = emptyList())

}