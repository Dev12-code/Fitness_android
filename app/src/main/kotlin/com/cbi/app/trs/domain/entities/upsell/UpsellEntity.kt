package com.cbi.app.trs.domain.entities.upsell

import com.cbi.app.trs.data.entities.MovieData
import com.cbi.app.trs.data.entities.UpsellData
import com.cbi.app.trs.domain.entities.BaseEntities

data class UpsellEntity(val data: Data) : BaseEntities() {
    data class Data(val total: Int, val max_page: Int, val page: Int, val limit: Int, val list_data: List<UpsellData>)
    companion object {
        fun empty() = UpsellEntity(Data(0, 0, 0, 0, emptyList()))
    }
}