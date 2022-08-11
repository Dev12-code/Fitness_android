package com.cbi.app.trs.features.fragments.payment.price

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.*
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.android.billingclient.api.*
import com.cbi.app.trs.R
import com.cbi.app.trs.core.exception.Failure
import com.cbi.app.trs.core.extension.*
import com.cbi.app.trs.core.navigation.Navigator
import com.cbi.app.trs.core.platform.BaseActivity
import com.cbi.app.trs.data.entities.PaymentCacheData
import com.cbi.app.trs.data.entities.UserData
import com.cbi.app.trs.domain.entities.BaseEntities
import com.cbi.app.trs.domain.entities.payment.SubsProductEntity
import com.cbi.app.trs.domain.eventbus.GetProfileEvent
import com.cbi.app.trs.domain.eventbus.PaymentPurchasedEvent
import com.cbi.app.trs.domain.usecases.payment.GetProductList
import com.cbi.app.trs.domain.usecases.payment.PostPurchaseToken
import com.cbi.app.trs.features.dialog.DialogAlert
import com.cbi.app.trs.features.dialog.NoInternetDialog
import com.cbi.app.trs.features.fragments.payment.PaymentBaseFragment
import com.cbi.app.trs.features.utils.AppConstants
import com.cbi.app.trs.features.utils.AppLog
import com.cbi.app.trs.features.viewmodel.PaymentViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.android.synthetic.main.price_fragment.*
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class PriceFragment : PaymentBaseFragment() {
    private var payMethod = PayMethod.NONE

    var itemSize = (Resources.getSystem().displayMetrics.widthPixels - 90.dp2px) / 2

    override fun layoutId(): Int {
        return R.layout.price_fragment
    }

    var networkCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            AppLog.e("Duy", "onAvailable")

            paymentCache.get()?.purchase?.let {
                if (userID == it.userId) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                            .setPurchaseToken(it.purchaseToken)
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams.build()) {
                    }
                    forceShowProgress()
                    paymentViewModel.sendPurchaseToken(userID, PostPurchaseToken.Params(it.purchaseProductId, it.purchaseToken))
                }
            }
        }

        override fun onLost(network: Network) {
            AppLog.e("Duy", "onLost")
        }
    }

    companion object {
        const val ALLOW_TO_FINISH_VIEW = "ALLOW_TO_FINISH_VIEW"
    }

    @Inject
    internal lateinit var navigator: Navigator

    lateinit var paymentViewModel: PaymentViewModel

    var selectedSku: SkuDetails? = null

    private var params: GetProductList.Params = GetProductList.Params(AppConstants.PLATFORM_ANDROID)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setScreenMode()
        initView()
    }

    override fun onSkuDetailsResponse(p0: BillingResult, p1: MutableList<SkuDetails>?) {
        super.onSkuDetailsResponse(p0, p1)
        getMonthlyPlan(skuList)?.let {
            pay_for_month_price.text = it.price
        }

        getAnnualPlan(skuList)?.let {
            pay_for_year_price.text = it.price
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        paymentViewModel = viewModel(viewModelFactory) {
            failure(failureData, ::handleFailure)
            observe(productList, ::onReceiveProductList)
            observe(purchaseTokenData, ::onSendPurchaseToken)
            observe(userProfile, ::onReceiveUserProfile)
        }
    }

    override fun onResume() {
        super.onResume()
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            val request = NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
            connectivityManager.registerNetworkCallback(request, networkCallback)
        }

    }

    override fun removeFreeTrialText() {
        try {
            super.removeFreeTrialText()
            tv_description.text = getString(R.string.which_plan_do_you_want)
            ll_container_free_trial_month.visibility = GONE
            ll_container_free_trial_year.visibility = GONE
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    override fun onPause() {
        super.onPause()
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun onReceiveUserProfile(userProfile: UserData.UserProfile?) {
        forceHide()
        //send event bus to refresh adapter
        EventBus.getDefault().postSticky(PaymentPurchasedEvent())
    }

    private fun onSendPurchaseToken(baseEntities: BaseEntities?) {
        forceHide()
        if (baseEntities == null) return
        if (baseEntities.isSuccess) {
            paymentCache.update(null)
            DialogAlert()
                    .setTitle("Payment Successful !")
                    .setMessage("Thank you! Payment received.")
                    .setCancel(false)
                    .setTitlePositive("OK")
                    .onPositive {
                        navigator.showLanding(activity)
                        EventBus.getDefault().post(GetProfileEvent())
                        activity?.finish()
                    }
                    .show(activity)
            paymentViewModel.getUserProfile(userID)
        } else {
            DialogAlert()
                    .setTitle("Sorry, Payment Failed!")
                    .setMessage("Payment failed. Please try another payment method")
                    .setCancel(false)
                    .setTitlePositive("OK")
                    .show(activity)
        }
    }

    private fun onReceiveProductList(list: List<SubsProductEntity.Data>?) {
        forceHide()
        if (list == null) return
        val productIds = ArrayList<String>()
        for (product in list) {
            productIds.add(product.subscription_id)
        }
        skuList.clear()
        getSubscriptionListingDetails(productIds)
    }

    override fun handleFailure(failure: Failure?) {
        if (failure is Failure.NetworkConnection) {
            if (!(activity as BaseActivity).isShowNoInternet) {
                (activity as BaseActivity).isShowNoInternet = true
                activity?.let {
                    NoInternetDialog().apply {
                        onDismiss { (activity as BaseActivity).isShowNoInternet = false }
                    }.showNoDuplicate(it, "NoInternet")
                }
            }
            forceHide()
        } else {
            super.handleFailure(failure)
        }
    }

    override fun loadPaymentData() {
        forceShowProgress()
        paymentViewModel.getProductList(userID, GetProductList.Params())
//        onReceiveProductList(ArrayList<SubsProductEntity.Data>().apply {
//            add(SubsProductEntity.Data("", "", "mothly_subscription_live_1499"))
//            add(SubsProductEntity.Data("", "", "annually_subscription_live_15999"))
//        }
//        )
    }

    private fun initView() {
        imageView.loadFromLocal(R.drawable.iv_choose_your_plan)
        pay_for_month.setOnClickListener {
            if (skuList.size > 0) {
                selectedSku = getMonthlyPlan(skuList)
                payMethod = PayMethod.MONTH
                setScreenMode()


                //update UI
                iv_tick_year.visibility = View.INVISIBLE
                iv_tick_month.visibility = VISIBLE

                pay_for_year.setBackgroundResource(R.drawable.background_price_unselected)
                pay_for_month.setBackgroundResource(R.drawable.background_price_selected)
            }
        }
        pay_for_month.layoutParams.width = itemSize
        pay_for_month.layoutParams.height = itemSize

        pay_for_year.setOnClickListener {
            if (skuList.size > 1) {
                selectedSku = getAnnualPlan(skuList)
                payMethod = PayMethod.YEAR
                setScreenMode()

                //update UI
                iv_tick_year.visibility = VISIBLE
                iv_tick_month.visibility = View.INVISIBLE

                pay_for_year.setBackgroundResource(R.drawable.background_price_selected)
                pay_for_month.setBackgroundResource(R.drawable.background_price_unselected)
            }
        }
        pay_for_year.layoutParams.width = itemSize
        pay_for_year.layoutParams.height = itemSize

        pay_btn.setOnClickListener {
            //validate type first
//            val userPlanPlatform = userDataCache.get()?.user_profile?.plan_platform
//            if (userPlanPlatform != AppConstants.PLAN_FREE_USER && userPlanPlatform != AppConstants.PLAN_ANDROID) {
//                var plan = ""
//                when (userPlanPlatform) {
//                    AppConstants.PLAN_ANDROID -> {
//                        plan = "Google Play"
//                    }
//                    AppConstants.PLAN_IOS -> {
//                        plan = "Apple Store"
//                    }
//                    AppConstants.PLAN_WEB -> {
//                        plan = "Website"
//                    }
//                    else -> {
//
//                    }
//                }
//                DialogAlert()
//                        .setTitle(getString(R.string.subcription))
//                        .setMessage(String.format(getString(R.string.subcription_user_plan_platform_message), plan))
//                        .setCancel(false)
//                        .setTitlePositive("OK")
//                        .onPositive {
//                            if (userPlanPlatform == AppConstants.PLAN_WEB) {
//                                DialogAlert()
//                                        .setTitle(getString(R.string.subcription))
//                                        .setMessage(String.format(getString(R.string.do_you_want_to_be_redirect_now), plan))
//                                        .setCancel(false)
//                                        .setTitleNegative(getString(R.string.btn_cancel))
//                                        .setTitlePositive("OK")
//                                        .onPositive {
//                                            handleCancelSubs(userDataCache.get()?.user_profile)
//                                        }
//                                        .onNegative {
//
//                                        }
//                                        .show(activity)
//                            }
//                        }
//                        .show(activity)
//                return@setOnClickListener
//            }
            selectedSku?.let {
                val flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(it)
                        .build()
                activity?.let { it1 -> billingClient.launchBillingFlow(it1, flowParams) }
            }
        }
        skip_btn.setOnClickListener {
            DialogAlert()
                    .setTitle(getString(R.string.are_you_sure_title))
                    .setMessage(getString(R.string.price_alert_skip_message))
                    .setCancel(false)
                    .setTitleNegative(getString(R.string.action_cancel))
                    .setTitlePositive("OK")
                    .onPositive {
                        navigator.showLanding(activity)
                        activity?.finish()
                    }
                    .show(activity)
        }
        skip_btn.extendTouch()
        back_btn.setOnClickListener {
            if (payMethod != PayMethod.NONE) {
                payMethod = PayMethod.NONE
                setScreenMode()
            } else {
                if (arguments == null) {
                    pop(activity)
                    return@setOnClickListener
                }
                arguments?.let {
                    val isExit = it.getBoolean(ALLOW_TO_FINISH_VIEW, false)
                    if (isExit) {
                        activity?.finish()
                    }
                }
            }

        }

        iv_arrow.extendTouch()
        iv_arrow.setOnClickListener {
            if (expandable_layout.isExpanded) {
                iv_arrow.rotation = 360f
            } else {
                iv_arrow.rotation = 180f
            }
            expandable_layout.isExpanded =
                    !expandable_layout.isExpanded
        }

        tv_term.setOnClickListener {
            navigator.showWebBrowser(activity, "https://thereadystate.com/policies/")
        }

        tv_policy.setOnClickListener {
            navigator.showWebBrowser(activity, "https://thereadystate.com/policies/")
        }
    }

    private fun handleCancelSubs(userProfile: UserData.UserProfile?) {
        if (userProfile?.cancel_subscription_link == null) return
        Intent(Intent.ACTION_VIEW, Uri.parse(userProfile.cancel_subscription_link)).apply {
            try {
                activity?.startActivity(this)
            } catch (e: Exception) {

            }
        }
    }

    override fun onProductPurchased(purchase: Purchase) {
        super.onProductPurchased(purchase)
        forceShowProgress()
        if (!purchase.isAcknowledged) {
            val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
            billingClient.acknowledgePurchase(acknowledgePurchaseParams.build()) {
                paymentCache.update(PaymentCacheData.PurchaseInfo(userID, purchase.sku, purchase.purchaseToken))
                paymentViewModel.sendPurchaseToken(userID, PostPurchaseToken.Params(purchase.sku, purchase.purchaseToken))
            }
        }
    }

    override fun onBackPressed(): Boolean {
        if (payMethod != PayMethod.NONE) {
            payMethod = PayMethod.NONE
            setScreenMode()
            return true
        }
        return super.onBackPressed()
    }

    private fun setScreenMode() {
        when (payMethod) {
            PayMethod.NONE -> {
                pay_area.visibility = GONE
                price_option_area.visibility = VISIBLE
            }
            PayMethod.MONTH -> {
                pay_area.visibility = VISIBLE
                price_option_area.visibility = GONE
                pay_price.text = "${selectedSku?.price}"
                pay_period.text = "/month"
            }
            PayMethod.YEAR -> {
                pay_area.visibility = VISIBLE
                price_option_area.visibility = GONE
                pay_price.text = "${selectedSku?.price}"
                pay_period.text = "/year"
            }
        }
    }


    enum class PayMethod {
        NONE,
        MONTH,
        YEAR
    }
}