package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentRequestModel(
    var order_id: String = "",
    var transaction_id: String = "",
    var amount: String = "",
    var payment_method: String = "online",
    var payment_for: String = "",
    var status: String = "",
    var billing_info: BillingInfoModel? = BillingInfoModel(),
) {
    @JsonClass(generateAdapter = true)
    data class BillingInfoModel(
        var name: String? = "",
        var address: String? = "",
        var city: String? = "",
        var state: String? = "",
        var landmark: String? = "",
        var pincode: String? = "",
    )
}
