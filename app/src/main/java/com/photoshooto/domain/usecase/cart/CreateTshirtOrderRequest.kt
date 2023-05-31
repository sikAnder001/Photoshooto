package com.photoshooto.domain.usecase.cart

import com.photoshooto.domain.usecase.product_details.AddToCartStandeeElement
import com.photoshooto.domain.usecase.product_details.TshirtElement
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CreateTshirtOrderRequest(
    // val coupon_code: String,
    val delivery_address: String? = null,
    val coupon_code: String? = null,
    val order_details: OrderDetails? = null,
    val tshirt_details: TshirtElement? = null,
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
