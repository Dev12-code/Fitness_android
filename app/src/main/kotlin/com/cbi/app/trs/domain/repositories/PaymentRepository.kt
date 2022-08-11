package com.cbi.app.trs.domain.repositories

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.platform.NetworkHandler
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.payment.SubsProductEntity
import com.cbi.app.trs.domain.network.BaseNetwork
import com.cbi.app.trs.domain.services.PaymentService
import com.cbi.app.trs.domain.usecases.payment.PostPurchaseToken
import javax.inject.Inject

interface PaymentRepository {
    fun postPurchaseToken(
        userID: Int?,
        param: PostPurchaseToken.Params
    ): Either<Failure, BaseEntities>

    fun getSubsProductList(
        userID: Int,
        platform: String
    ): Either<Failure, List<SubsProductEntity.Data>>

    class Network
    @Inject constructor(
        private val networkHandler: NetworkHandler,
        private val service: PaymentService
    ) : PaymentRepository, BaseNetwork() {

        override fun postPurchaseToken(
            userID: Int?,
            param: PostPurchaseToken.Params
        ): Either<Failure, BaseEntities> {
            return when (networkHandler.isConnected) {
                true -> request(service.sendPurchaseToken(userID, param), {
                    it
                }, BaseEntities())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }

        override fun getSubsProductList(
            userID: Int,
            platform: String
        ): Either<Failure, List<SubsProductEntity.Data>> {
            return when (networkHandler.isConnected) {
                true -> request(service.getProductList(userID, platform), {
                    it.data
                }, SubsProductEntity.empty())
                false, null -> Either.Left(Failure.NetworkConnection)
            }
        }
    }
}