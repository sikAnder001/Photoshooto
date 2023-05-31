package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetStandeeResponse(
    val data: StandeeData? = null,
    val message: String,
    val success: Boolean
)

@JsonClass(generateAdapter = true)
data class StandeeData(
    val list: List<StandeeElement>? = null,
    val next_page: Int,
    val page: Int
)

@JsonClass(generateAdapter = true)
data class StandeeElement(
    val _id: String,
    val created_at: String,
    val description: String,
    val discount: Double,
    val height: String,
    val id: String,
    val images: List<String>? = null,
    val price: Double,
    val type: String,
    val updated_at: String,
    val user_profile: Any? = null,
    val weight: String,
    val width: String,
    val min_quantity: Int,
    var qrcode: List<Qrcode?>? = listOf(),
)

@JsonClass(generateAdapter = true)
data class Qrcode(
    var _id: String? = "",
    var expiry: String? = "",
    var id: String? = "",
    var url: String? = "",
    var user_id: String? = "",
    var __v: String? = "",
    var created_at: String? = "",
    var updated_at: String? = ""
)