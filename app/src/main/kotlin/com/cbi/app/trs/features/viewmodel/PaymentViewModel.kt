package com.cbi.app.trs.features.viewmodel

import androidx.lifecycle.MutableLiveData
import com.cbi.app.trs.core.interactor.UseCase
import com.cbi.app.trs.core.platform.BaseViewModel
import com.cbi.app.trs.data.entities.ReviewData
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.payment.SubsProductEntity
import com.cbi.app.trs.domain.usecases.payment.GetProductList
import com.cbi.app.trs.domain.usecases.payment.PostPurchaseToken
import com.cbi.app.trs.domain.usecases.system.GetReview
import com.cbi.app.trs.domain.usecases.user.GetUserProfile
import javax.inject.Inject

class PaymentViewModel
@Inject constructor(
    private val getUserProfile: GetUserProfile,
    private val postPurchaseToken: PostPurchaseToken,
    private val getProductList: GetProductList,
    private val getReview: GetReview
) : BaseViewModel() {
    var productList: MutableLiveData<List<SubsProductEntity.Data>> = MutableLiveData()
    var purchaseTokenData: MutableLiveData<BaseEntities> = MutableLiveData()
    var reviewData: MutableLiveData<List<ReviewData>> = MutableLiveData()
    var userProfile: MutableLiveData<UserData.UserProfile> = MutableLiveData()

    fun getReviewData() = getReview(UseCase.None()) { it.fold(::handleFailure, ::handleReview) }

    private fun handleReview(list: List<ReviewData>) {
        this.reviewData.value = list
    }

    fun getProductList(userId: Int, param: GetProductList.Params) =
        getProductList(Pair(userId, param)) { it.fold(::handleFailure, ::handleProductList) }

    private fun handleProductList(list: List<SubsProductEntity.Data>) {
        this.productList.value = list
    }

    fun getUserProfile(userID: Int) =
        getUserProfile(userID) { it.fold(::handleFailure, ::handleUserProfile) }

    private fun handleUserProfile(userProfile: UserData.UserProfile) {
        this.userProfile.value = userProfile
    }

    fun sendPurchaseToken(userId: Int?, param: PostPurchaseToken.Params) =
        postPurchaseToken(Pair(userId, param)) {
            it.fold(
                ::handleFailure,
                ::handleSendPurchaseToken
            )
        }

    private fun handleSendPurchaseToken(baseEntities: BaseEntities) {
        this.purchaseTokenData.value = baseEntities
    }
}