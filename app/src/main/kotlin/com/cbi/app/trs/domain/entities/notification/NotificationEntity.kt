package com.cbi.app.trs.domain.entities.notification

import com.cbi.app.trs.data.entities.NotificationData
import com.cbi.app.trs.domain.entities.BaseEntities

data class NotificationEntity(val data: Data) : BaseEntities() {
    data class Data(val total: Int = 0, val max_page: Int = 0, val page: Int = 0, val limit: Int = 0, val data: List<NotificationData> = ArrayList())
    companion object {
        fun empty() = NotificationEntity(Data())
    }
}