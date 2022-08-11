package com.cbi.app.trs.domain.entities.system

import com.cbi.app.trs.domain.entities.BaseEntities

data class SystemAchievementEntity(
        val data: ArrayList<Data>
) : BaseEntities() {
    companion object {
        fun empty() = SystemAchievementEntity(ArrayList())
    }

    data class Data(
            val achievement_id: Int,
            val achievement_title: String,
            val achievement_description: String?,
            val achievement_milestone: Int?
    )
}
