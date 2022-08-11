package com.cbi.app.trs.domain.entities.system

import com.cbi.app.trs.core.extension.empty
import com.cbi.app.trs.domain.entities.BaseEntities

data class SystemConfigEntity(
        val data: Data
) : BaseEntities() {
    companion object {
        fun empty() = SystemConfigEntity(Data(String.empty(), String.empty(), String.empty()))
    }

    data class Data(
            val minimum_version_ios: String,
            val minimum_version_android: String,
            val force_update_message: String
    )
}

