package com.cbi.app.trs.domain.usecases.notification

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.repositories.NotificationRepository
import javax.inject.Inject

class MarkNotification @Inject constructor(private val notificationRepository: NotificationRepository) : UseCase<BaseEntities, Pair<Int?, MarkNotification.Params>>() {
    data class Params(val notification_id: Int?)

    override suspend fun run(pair: Pair<Int?, Params>) = notificationRepository.markNotification(pair.first, pair.second)
}