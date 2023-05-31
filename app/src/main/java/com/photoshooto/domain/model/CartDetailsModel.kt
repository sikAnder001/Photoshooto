package com.photoshooto.domain.model

import com.photoshooto.R
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class CartDetailsModel(
    val data: CartData? = null,
    val message: String? = null,
    var success: Boolean? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class CartData(
    val products: ArrayList<CartProduct>? = null,
    val address: DeliverAddress? = null,
    val subTotal: Double = 0.0,
    val couponSaving: Double = 0.0,
    val tax: Double = 0.0,
    val shipping: Double = 0.0,
    val amountPayable: Double = 0.0
) : Serializable

@JsonClass(generateAdapter = true)
data class CartProduct(
    val name: String? = null,
    val sortDescription: String? = null,
    val orderId: String? = null,
    val image: Int? = R.drawable.img_temp_product_tshirt,
    val addedSize: ArrayList<CartProductSize>? = null,
    val availableSize: ArrayList<String>? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class CartProductSize(
    val name: String? = null,
    var qty: Int? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class DeliverAddress(
    val name: String? = null,
    val mobileNumber: String? = null,
    val addressLine1: String? = null,
    val addressLine2: String? = null
) : Serializable
