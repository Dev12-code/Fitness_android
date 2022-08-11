package com.cbi.app.trs.domain.entities.workout

import com.cbi.app.trs.data.entities.CategoryData
import com.cbi.app.trs.domain.entities.BaseEntities

data class CategoryEntity(val data: List<CategoryData> = ArrayList()) : BaseEntities()