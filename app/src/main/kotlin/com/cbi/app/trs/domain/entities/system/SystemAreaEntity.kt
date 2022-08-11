package com.cbi.app.trs.domain.entities.system

import com.cbi.app.trs.domain.entities.BaseEntities

data class SystemAreaEntity(
        val data: ArrayList<Data>
) : BaseEntities() {
    companion object {
        fun empty() = SystemAreaEntity(ArrayList())
    }

    data class Data(
            val area_title: String,
            val area_id: Int
    )
}
