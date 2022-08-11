package com.cbi.app.trs.domain.usecases.notification

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.notification.NotificationEntity
import com.cbi.app.trs.domain.repositories.NotificationRepository
import com.cbi.app.trs.domain.usecases.PagingParam
import javax.inject.Inject

class GetNotification @Inject constructor(private val notificationRepository: NotificationRepository) : UseCase<NotificationEntity.Data, Pair<Int?, PagingParam>>() {

    override suspend fun run(param: Pair<Int?, PagingParam>) = notificationRepository.getNotification(param.first, param.second)
}