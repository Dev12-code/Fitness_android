package com.cbi.app.trs.domain.entities.setting

import com.cbi.app.trs.data.entities.HtmlData
import com.cbi.app.trs.domain.entities.BaseEntities

data class HtmlEntity(val data: HtmlData) : BaseEntities() {
    companion object {
        fun empty() = HtmlEntity(HtmlData(""))
    }
}