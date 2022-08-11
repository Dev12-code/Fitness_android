package com.cbi.app.trs.features.fragments.payment

import android.os.Bundle
import com.android.billingclient.api.*
import com.cbi.app.trs.core.platform.BaseFragment
import com.cbi.app.trs.features.utils.AppLog
import com.cbi.app.trs.features.utils.CommonUtils


abstract class PaymentBaseFragment : BaseFragment(), SkuDetailsResponseListener {
    var skuList: MutableList<SkuDetails> = mutableListOf()

    private val purchaseUpdateListener =
            PurchasesUpdatedListener { billingResult, purchases ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                    for (purchase in purchases) {
                        onProductPurchased(purchase)
                    }
                } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user cancelling the purchase flow.
                } else {
                    // Handle any other error codes.
                }
            }

    lateinit var billingClient: BillingClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        billingClient = BillingClient.newBuilder(requireContext())
                .setListener(purchaseUpdateListener)
                .enablePendingPurchases()
                .build()
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    AppLog.e("Duy", "onBillingInitialized")
                    loadPaymentData()
                    queryHistoryPurchasePackage()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    private fun queryHistoryPurchasePackage(){
        billingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchasesList != null) {
                if (purchasesList.isNotEmpty()){
                    removeFreeTrialText()
                }
            }
        }
    }

    open fun removeFreeTrialText(){

    }

    override fun onReloadData() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    AppLog.e("Duy", "onBillingInitialized")
                    loadPaymentData()
                    queryHistoryPurchasePackage()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    abstract fun loadPaymentData()

    open fun onProductPurchased(purchase: Purchase) {
        AppLog.e("Duy", "onProductPurchased")
    }


    fun getSubscriptionListingDetails(productIds: ArrayList<String>) {
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(productIds).setType(BillingClient.SkuType.SUBS)
        billingClient.querySkuDetailsAsync(params.build(), this)
    }

    override fun onSkuDetailsResponse(p0: BillingResult, p1: MutableList<SkuDetails>?) {
        p1?.let { skuList = it }
        if (skuList.size == 0) {
            CommonUtils.showError(activity, "Payment", "Can't get payment information from Google Play. Please try later.")
            return
        }
    }

    fun getAnnualPlan(list: MutableList<SkuDetails>): SkuDetails? {
        for (i in list) {
            if (i.sku.contains("annually")) return i
        }
        return null
    }

    fun getMonthlyPlan(list: MutableList<SkuDetails>): SkuDetails? {
        for (i in list) {
            if (i.sku.contains("mothly") || i.sku.contains("monthly")) return i
        }
        return null
    }
}