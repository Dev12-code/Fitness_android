package com.cbi.app.trs.domain.entities.system

import com.cbi.app.trs.domain.entities.BaseEntities

data class SystemCollectionEntity(
        val data: ArrayList<Data>
) : BaseEntities() {
    companion object {
        fun empty() = SystemCollectionEntity(ArrayList())
    }

    data class Data(
            val collection_title: String,
            val collection_id: Int
    )
}
