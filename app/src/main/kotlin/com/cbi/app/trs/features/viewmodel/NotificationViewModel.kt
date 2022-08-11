package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.notification.NotificationEntity
import com.cbi.app.trs.domain.usecases.PagingParam
import com.cbi.app.trs.domain.usecases.notification.GetNotification
import com.cbi.app.trs.domain.usecases.notification.MarkNotification
import com.cbi.app.trs.domain.usecases.notification.RegisterNotification
import javax.inject.Inject

class NotificationViewModel @Inject constructor(private val getNotification: GetNotification,
                                                private val markNotification: MarkNotification,
                                                private val registerNotification: RegisterNotification) : BaseViewModel() {
    var notificationData: MutableLiveData<NotificationEntity.Data> = MutableLiveData()
    var markNotificationData: MutableLiveData<BaseEntities> = MutableLiveData()
    var registerNotificationData: MutableLiveData<BaseEntities> = MutableLiveData()

    fun getNotification(userId: Int?, pagingParam: PagingParam) = getNotification(Pair(userId, pagingParam)) { it.fold(::handleFailure, ::handleGetNotification) }

    fun markNotification(pair: Pair<Int?, MarkNotification.Params>) = markNotification(pair) { it.fold(::handleFailure, ::handleMarkNotification) }

    fun registerNotification(pair: Pair<Int?, RegisterNotification.Params>) = registerNotification(pair) { it.fold(::handleFailure, ::handleRegisterNotification) }

    private fun handleGetNotification(data: NotificationEntity.Data) {
        notificationData.value = data
    }

    private fun handleMarkNotification(baseEntities: BaseEntities) {
        markNotificationData.value = baseEntities
    }

    private fun handleRegisterNotification(baseEntities: BaseEntities) {
        registerNotificationData.value = baseEntities
    }
}