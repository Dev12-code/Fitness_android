package com.cbi.app.trs.domain.api

import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.notification.NotificationEntity
import com.cbi.app.trs.domain.usecases.PagingParam
import com.cbi.app.trs.domain.usecases.notification.MarkNotification
import com.cbi.app.trs.domain.usecases.notification.RegisterNotification
import retrofit2.Call
import retrofit2.http.*

internal interface NotificationAPI {
    companion object {
        private const val UserID = "UserID"
        private const val MovieID = "MovieID"
        private const val GET_NOTIFICATION = "{$UserID}/notifications"
        private const val MARK_NOTIFICATION = "{$UserID}/notifications/mark"
        private const val REGISTER_NOTIFICATION = "{$UserID}/notifications/register"
    }

    @POST(GET_NOTIFICATION)
    fun getNotification(@Path(UserID) userID: Int?, @Body params: PagingParam): Call<NotificationEntity>

    @POST(MARK_NOTIFICATION)
    fun markNotification(@Path(UserID) userID: Int?, @Body params: MarkNotification.Params): Call<BaseEntities>


    @POST(REGISTER_NOTIFICATION)
    fun registerNotification(@Path(UserID) userID: Int?, @Body params: RegisterNotification.Params): Call<BaseEntities>
}