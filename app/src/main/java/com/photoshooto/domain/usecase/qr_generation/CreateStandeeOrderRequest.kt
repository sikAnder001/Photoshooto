package com.photoshooto.domain.usecase.qr_generation

import com.photoshooto.domain.usecase.product_details.AddToCartStandeeElement
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateStandeeOrderRequest(
    // val coupon_code: String,
    val project_id: String? = null,
    val qrcode_id: String? = null,
    val delivery_address: String? = null,
    val coupon_code: String? = null,
    val order_details: OrderDetails? = null,
    val standee_details: AddToCartStandeeElement? = null
)

@JsonClass(generateAdapter = true)
data class OrderDetails(
    val coupon_saving: Double = 0.0,
    var gst_tax: Double = 0.0,
    val shipping: Double = 0.0,
    var sub_total: Double = 0.0,
    var total_amount: Double = 0.0,
    var total_units: Int = 0
)
