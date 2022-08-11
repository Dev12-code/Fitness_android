package com.cbi.app.trs.domain.services

import com.cbi.app.trs.domain.api.NotificationAPI
import com.cbi.app.trs.domain.usecases.PagingParam
import com.cbi.app.trs.domain.usecases.notification.MarkNotification
import com.cbi.app.trs.domain.usecases.notification.RegisterNotification
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationService
@Inject constructor(retrofit: Retrofit) : NotificationAPI {
    private val notificationAPI by lazy { retrofit.create(NotificationAPI::class.java) }
    override fun getNotification(userID: Int?, params: PagingParam) = notificationAPI.getNotification(userID, params)
    override fun markNotification(userID: Int?, params: MarkNotification.Params) = notificationAPI.markNotification(userID, params)
    override fun registerNotification(userID: Int?, params: RegisterNotification.Params) = notificationAPI.registerNotification(userID, params)
}