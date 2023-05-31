package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class CouponsModel(
    val data: ArrayList<CouponData>? = null,
    val message: String? = null,
    var success: Boolean? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class CouponData(
    val icon: Int? = null,
    val title: String? = null,
    val description: String? = null,
    val couponCode: String? = null
) : Serializable


