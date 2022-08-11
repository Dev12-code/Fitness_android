package com.cbi.app.trs.domain.repositories

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.platform.NetworkHandler
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.notification.NotificationEntity
import com.cbi.app.trs.domain.network.BaseNetwork
import com.cbi.app.trs.domain.services.NotificationService
import com.cbi.app.trs.domain.usecases.PagingParam
import com.cbi.app.trs.domain.usecases.notification.MarkNotification
import com.cbi.app.trs.domain.usecases.notification.RegisterNotification
import javax.inject.Inject

interface NotificationRepository {
    fun getNotification(userId: Int?, params: PagingParam): Either<Failure, NotificationEntity.Data>
    fun markNotification(userId: Int?, params: MarkNotification.Params): Either<Failure, BaseEntities>
    fun registerNotification(userId: Int?, params: RegisterNotification.Params): Either<Failure, BaseEntities>

    class Network
    @Inject constructor(private val networkHandler: NetworkHandler,
                        private val service: NotificationService) : NotificationRepository, BaseNetwork() {

        override fun getNotification(userId: Int?, params: PagingParam): Either<Failure, NotificationEntity.Data> {
            return when (networkHandler.isConnected) {
                true -> request(service.getNotification(userId, params), {
                    it.data
                }, NotificationEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun markNotification(userId: Int?, params: MarkNotification.Params): Either<Failure, BaseEntities> {
            return when (networkHandler.isConnected) {
                true -> request(service.markNotification(userId, params), {
                    it
                }, BaseEntities.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun registerNotification(userId: Int?, params: RegisterNotification.Params): Either<Failure, BaseEntities> {
            return when (networkHandler.isConnected) {
                true -> request(service.registerNotification(userId, params), {
                    it
                }, BaseEntities.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }
    }
}