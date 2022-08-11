package com.cbi.app.trs.data.entities

data class NotificationData(val notification_type: String = "", val notification_name: String = "", val notification_id: Int = 0, val notification_date: Long = 0, val notification_description: String = "", var notification_is_readed: Boolean = false) {
}