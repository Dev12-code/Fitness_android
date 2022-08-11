package com.cbi.app.trs.domain.entities.system

import com.cbi.app.trs.domain.entities.BaseEntities

data class SystemPostWorkoutEntity(
        val data: ArrayList<Data>
) : BaseEntities() {
    companion object {
        fun empty() = SystemPostWorkoutEntity(ArrayList())
    }

    data class Data(
            val post_title: String,
            val post_id: Int
    )
}
