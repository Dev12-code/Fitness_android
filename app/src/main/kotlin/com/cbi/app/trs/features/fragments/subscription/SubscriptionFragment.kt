package com.cbi.app.trs.features.fragments.subscription

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.cbi.app.trs.R
import com.cbi.app.trs.core.extension.*
import com.cbi.app.trs.core.platform.BaseActivity
import com.cbi.app.trs.data.entities.PaymentCacheData
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.payment.SubsProductEntity
import com.cbi.app.trs.domain.eventbus.PaymentPurchasedEvent
import com.cbi.app.trs.domain.usecases.payment.PostPurchaseToken
import com.cbi.app.trs.features.activities.MainActivity
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.fragments.payment.PaymentBaseFragment
import com.cbi.app.trs.features.utils.AppConstants
import com.cbi.app.trs.features.viewmodel.PaymentViewModel
import com.cbi.app.trs.features.viewmodel.UserProfileViewModel
import kotlinx.android.synthetic.main.fragment_subscription.*
import org.greenrobot.eventbus.EventBus

class SubscriptionFragment : PaymentBaseFragment() {
    override fun layoutId() = R.layout.fragment_subscription

    private var userProfile: UserData.UserProfile? = null
    lateinit var userProfileViewModel: UserProfileViewModel
    lateinit var paymentViewModel: PaymentViewModel
    private var isPurchasedSuccess: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        userProfileViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(userProfile, ::onReceiveUserProfile)
        }
        paymentViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(productList, ::onReceiveProductList)
            observe(purchaseTokenData, ::onSendPurchaseToken)
        }
    }

    override fun onReloadData() {
        showProgress()
        userProfileViewModel.getUserProfile(userID)
    }

    override fun onProductPurchased(purchase: Purchase) {
        showProgress()
        paymentCache.update(
            PaymentCacheData.PurchaseInfo(
                userID,
                purchase.sku,
                purchase.purchaseToken
            )
        )
        paymentViewModel.sendPurchaseToken(
            userID,
            PostPurchaseToken.Params(purchase.sku, purchase.purchaseToken)
        )
    }

    private fun onSendPurchaseToken(baseEntities: BaseEntities?) {
        hideProgress()
        if (baseEntities == null) return
        if (baseEntities.isSuccess) {
            paymentCache.update(null)
            DialogAlert()
                .setTitle("Payment Successful !")
                .setMessage("Thank you! Payment received.")
                .setCancel(false)
                .setTitlePositive("OK")
                .onPositive {
                    isPurchasedSuccess = true
                    showProgress()
                    userProfileViewModel.getUserProfile(userID)
                }
                .show(activity)
        } else {
            DialogAlert()
                .setTitle("Sorry, Payment Failed!")
                .setMessage("Payment failed. Please try another payment method")
                .setCancel(false)
                .setTitlePositive("OK")
                .show(activity)
        }
    }

    override fun loadPaymentData() {
        onReceiveProductList(ArrayList<SubsProductEntity.Data>().apply {
            add(SubsProductEntity.Data("", "", "mothly_subscription_live_1499"))
            add(SubsProductEntity.Data("", "", "annually_subscription_live_15999"))
        }
        )
    }

    private fun onReceiveUserProfile(userProfile: UserData.UserProfile?) {
        hideProgress()
        if (userProfile == null) return
        this.userProfile = userProfile
        loadUserPlan()
        if (isPurchasedSuccess) {
            //send event bus to refresh adapter
            EventBus.getDefault().postSticky(PaymentPurchasedEvent())
        }
    }

    private fun loadUserPlan() {
        if (userProfile == null) return
        user_plan.text = userProfile!!.user_plan
        user_plan_start.text = "${userProfile!!.plan_register_date?.getMonthYearDate("MM/dd/yyyy")}"
        user_plan_end.text = "${userProfile!!.plan_expire_date?.getMonthYearDate("MM/dd/yyyy")}"
//        if (userProfile!!.isMonthlyUser() || userProfile!!.isYearlyUser()) {
//            change_plan_btn.visibility = View.VISIBLE
//        }
        if (!userProfile!!.isFreeUser() && (userProfile!!.pending_cancel == null || userProfile!!.pending_cancel == 0)) {
            cancel_btn.visibility = View.VISIBLE
        } else {
            cancel_btn.visibility = View.GONE
        }
    }

    //    private fun changePlan() {
//        if (userProfile == null) return
//        if (userProfile!!.isMonthlyUser()) {
//            getAnnualPlan(skuList)?.let { bp.updateSubscription(activity, "mothly_subscription_live_1499", it.productId, BuildConfig.PLAY_CONSOLE_KEY) }
//            return
//        }
//        if (userProfile!!.isYearlyUser()) {
//            getMonthlyPlan(skuList)?.let { bp.updateSubscription(activity, "annually_subscription_live_15999", it.productId, BuildConfig.PLAY_CONSOLE_KEY) }
//            return
//        }
//
    override fun onSkuDetailsResponse(p0: BillingResult, p1: MutableList<SkuDetails>?) {
        super.onSkuDetailsResponse(p0, p1)
    }

    private fun onReceiveProductList(list: List<SubsProductEntity.Data>?) {
        if (list == null) return
        val productIds = ArrayList<String>()
        for (product in list) {
            productIds.add(product.subscription_id)
        }
        skuList.clear()
        getSubscriptionListingDetails(productIds)

    }

    private fun initView() {
        back_btn.setOnClickListener { close() }
        cancel_btn.setOnClickListener {
            //validate type first
            val userPlanPlatform = userDataCache.get()?.user_profile?.plan_platform
            if (userPlanPlatform != AppConstants.PLAN_FREE_USER && userPlanPlatform != AppConstants.PLAN_ANDROID) {
                var plan = ""
                when (userPlanPlatform) {
                    AppConstants.PLAN_ANDROID -> {
                        plan = "Google Play"
                    }
                    AppConstants.PLAN_IOS -> {
                        plan = "Apple Store"
                    }
                    AppConstants.PLAN_WEB -> {
                        plan = "Website"
                    }
                    else -> {

                    }
                }
                DialogAlert()
                    .setTitle(getString(R.string.subcription))
                    .setMessage(
                        String.format(
                            getString(R.string.cancel_user_plan_platform_message),
                            plan
                        )
                    )
                    .setCancel(false)
                    .setTitlePositive("OK")
                    .onPositive {
                    }
                    .show(activity)
                return@setOnClickListener
            }
            DialogAlert().setTitle("We're sorry to see you go!")
                .setMessage("You will be taken to the registration management page. Do you want to continue?")
                .setTitleNegative("Cancel").setTitlePositive("OK")
                .onPositive { handleCancelSubs() }.show(activity)
        }
//        change_plan_btn.setOnClickListener { changePlan() }
    }

    private fun handleCancelSubs() {
        if (userProfile == null || userProfile!!.cancel_subscription_link == null) return
        Intent(Intent.ACTION_VIEW, Uri.parse(userProfile!!.cancel_subscription_link)).apply {
            try {
                activity?.startActivity(this)
            } catch (e: Exception) {

            }
        }
    }

//    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).navigationView.visibility = View.GONE
        (activity as BaseActivity).changeFullScreenMode(false)
        showProgress()
        userProfileViewModel.getUserProfile(userID)
    }

    override fun onPause() {
        super.onPause()
        (activity as MainActivity).navigationView.visibility = View.VISIBLE
    }
}