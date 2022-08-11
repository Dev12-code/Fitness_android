package com.cbi.app.trs.data.entities

data class PaymentCacheData(var purchase: PurchaseInfo? = null) {

    data class PurchaseInfo(val userId: Int = -1, val purchaseProductId: String = "", val purchaseToken: String = "")
}