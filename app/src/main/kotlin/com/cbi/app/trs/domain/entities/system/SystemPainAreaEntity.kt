package com.cbi.app.trs.domain.entities.system

import com.cbi.app.trs.domain.entities.BaseEntities

data class SystemPainAreaEntity(
        val data: ArrayList<Data>
) : BaseEntities() {
    companion object {
        fun empty() = SystemPainAreaEntity(ArrayList())
    }

    data class Data(
            val pain_area_title: String,
            val pain_area_key: String,
            val pain_area_id: Int,
            val pain_area_type: String
    )
}
