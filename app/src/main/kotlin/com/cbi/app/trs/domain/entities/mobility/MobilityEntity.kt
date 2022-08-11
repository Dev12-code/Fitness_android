package com.cbi.app.trs.domain.entities.mobility

import com.cbi.app.trs.data.entities.MobilityStatus
import com.cbi.app.trs.domain.entities.BaseEntities

data class MobilityEntity(val data: MobilityStatus) : BaseEntities() {
    companion object {
        fun empty() = MobilityEntity(MobilityStatus(0.0, 0.0, 0.0, 0.0, 0L, false))
    }
}