package com.cbi.app.trs.domain.entities.system

import com.cbi.app.trs.data.entities.SystemData
import com.cbi.app.trs.domain.entities.BaseEntities

data class SystemBonusEntity(
        val data: ArrayList<SystemData.Bonus>
) : BaseEntities() {
    companion object {
        fun empty() = SystemBonusEntity(ArrayList())
    }
}
