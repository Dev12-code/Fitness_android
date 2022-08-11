package com.cbi.app.trs.domain.entities.system

import com.cbi.app.trs.domain.entities.BaseEntities

data class SystemPreWorkoutEntity(
        val data: ArrayList<Data>
) : BaseEntities() {
    companion object {
        fun empty() = SystemPreWorkoutEntity(ArrayList())
    }

    data class Data(
            val pre_title: String,
            val pre_id: Int
    )
}
