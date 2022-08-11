package com.cbi.app.trs.domain.usecases.notification

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.repositories.NotificationRepository
import javax.inject.Inject

class RegisterNotification @Inject constructor(private val notificationRepository: NotificationRepository) : UseCase<BaseEntities, Pair<Int?, RegisterNotification.Params>>() {
    data class Params(val device_id: String?, val device_token: String?, val device_type: String = "android")

    override suspend fun run(pair: Pair<Int?, Params>) = notificationRepository.registerNotification(pair.first, pair.second)
}