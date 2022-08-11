package com.cbi.app.trs.domain.usecases.payment

import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.repositories.AuthenticateRepository
import com.cbi.app.trs.domain.repositories.PaymentRepository
import javax.inject.Inject

class PostPurchaseToken
@Inject constructor(private val paymentRepository: PaymentRepository) : UseCase<BaseEntities, Pair<Int?, PostPurchaseToken.Params>>() {
    data class Params(val product_id: String = "", val purchase_token: String = "")

    override suspend fun run(params: Pair<Int?, Params>) = paymentRepository.postPurchaseToken(params.first, params.second)
}