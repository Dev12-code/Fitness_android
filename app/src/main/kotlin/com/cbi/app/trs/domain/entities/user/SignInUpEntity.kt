package com.cbi.app.trs.domain.entities.user

import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities

data class SignInUpEntity(val message_code: Int?, val data: UserData?) : BaseEntities() {
    companion object {
        fun empty() = SignInUpEntity(0, null)
    }
}