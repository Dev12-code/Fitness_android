package com.cbi.app.trs.domain.services

import com.cbi.app.trs.domain.api.PainAPI
import com.cbi.app.trs.domain.api.PaymentAPI
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.payment.SubsProductEntity
import com.cbi.app.trs.domain.usecases.pain.GetUnderstandingPain
import com.cbi.app.trs.domain.usecases.payment.GetProductList
import com.cbi.app.trs.domain.usecases.payment.PostPurchaseToken
import retrofit2.Call
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentService
@Inject constructor(retrofit: Retrofit) : PaymentAPI {
    private val paymentAPI by lazy { retrofit.create(PaymentAPI::class.java) }
    override fun sendPurchaseToken(userID: Int?, params: PostPurchaseToken.Params) = paymentAPI.sendPurchaseToken(userID, params)
    override fun getProductList(userID: Int, platform: String): Call<SubsProductEntity> {
        return paymentAPI.getProductList(userID, platform)
    }
}