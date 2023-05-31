package com.photoshooto.domain.usecase.payment_success

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentSuccessRequest(
    var transcation_id: String? = null,
    var amount: String? = null,
    var payment_method: String? = null,
    var payment_for: String? = null,
    var status: String? = null,
    var billing_info: BillingInfo? = null
)

@JsonClass(generateAdapter = true)
data class BillingInfo(
    var name: String? = null,
    var address: String? = null,
    var city: String? = null,
    var state: String? = null,
    var landmark: String? = null,
    var pincode: String? = null
)