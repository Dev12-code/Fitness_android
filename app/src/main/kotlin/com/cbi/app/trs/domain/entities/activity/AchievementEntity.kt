package com.cbi.app.trs.domain.entities.activity

import com.cbi.app.trs.data.entities.AchievementData
import com.cbi.app.trs.domain.entities.BaseEntities

data class AchievementEntity(val data: Data) : BaseEntities() {
    data class Data(val user_streak_point: Int, val user_archiverments: List<AchievementData>)
    companion object {
        fun empty() = AchievementEntity(Data(0, ArrayList()))
    }
}