package com.cbi.app.trs.domain.api

import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.payment.SubsProductEntity
import com.cbi.app.trs.domain.usecases.payment.PostPurchaseToken
import retrofit2.Call
import retrofit2.http.*

internal interface PaymentAPI {
    companion object {
        private const val UserID = "UserID"
        private const val POST_PURCHASE_TOKEN = "{$UserID}/product/transaction/googleplay"
        private const val GET_PRODUCT_LIST = "{$UserID}/product/subscription_user"
    }

    @POST(POST_PURCHASE_TOKEN)
    fun sendPurchaseToken(
        @Path(UserID) userID: Int?,
        @Body params: PostPurchaseToken.Params
    ): Call<BaseEntities>

    @GET(GET_PRODUCT_LIST)
    fun getProductList(
        @Path(UserID) userID: Int,
        @Query("platform") platform: String
    ): Call<SubsProductEntity>
}