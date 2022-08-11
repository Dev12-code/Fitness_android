package com.cbi.app.trs.domain.usecases.payment

import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.functional.Either
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.domain.entities.payment.SubsProductEntity
import com.cbi.app.trs.domain.repositories.PaymentRepository
import javax.inject.Inject

class GetProductList
@Inject constructor(private val paymentRepository: PaymentRepository) :
    UseCase<List<SubsProductEntity.Data>, Pair<Int, GetProductList.Params>>() {
    data class Params(val platform: String = "Android")

    override suspend fun run(params: Pair<Int, Params>): Either<Failure, List<SubsProductEntity.Data>> {
        val queries = params.second
        return paymentRepository.getSubsProductList(params.first, queries.platform)
    }

}