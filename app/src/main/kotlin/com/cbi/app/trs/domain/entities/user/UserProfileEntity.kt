package com.cbi.app.trs.domain.entities.user

import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities

data class UserProfileEntity(val data: UserData.UserProfile?) : BaseEntities() {
    companion object {
        fun empty() = UserProfileEntity(null)
    }
}