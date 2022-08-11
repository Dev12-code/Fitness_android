package com.cbi.app.trs.domain.entities.payment

import com.cbi.app.trs.domain.entities.BaseEntities

data class SubsProductEntity(val data: List<Data>) : BaseEntities() {
    data class Data(val title: String, val billing_period: String, val subscription_id: String)
    companion object {
        fun empty() = SubsProductEntity(ArrayList())
    }
}