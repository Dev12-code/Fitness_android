package com.cbi.app.trs.domain.entities.system

import com.cbi.app.trs.data.entities.ReviewData
import com.cbi.app.trs.domain.entities.BaseEntities

data class ReviewEntity(val data: List<ReviewData> = emptyList()) : BaseEntities()