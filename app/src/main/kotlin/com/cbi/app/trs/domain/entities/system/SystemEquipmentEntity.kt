package com.cbi.app.trs.domain.entities.system

import com.cbi.app.trs.domain.entities.BaseEntities

data class SystemEquipmentEntity(
        val data: ArrayList<Data>
) : BaseEntities() {
    companion object {
        fun empty() = SystemEquipmentEntity(ArrayList())
    }

    data class Data(
            val equipment_title: String,
            val equipment_id: Int
    )
}
