package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDetailsModel(
    val data: ProductData? = null,
    val message: String? = null,
    var success: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class TshirtProductElement(
    val _id: String,
    val color: String,
    val created_at: String,
    val description: String,
    val discount: Int,
    val id: String,
    val images: List<String>? = null,
    val price: Int,
    val properties: List<String>? = null,
    val sizes: List<TshirtSize>? = null,
    val updated_at: String,
    val user_profile: Any?
)

@JsonClass(generateAdapter = true)
data class TshirtSize(
    val is_avilable: Boolean,
    val size: String
)

@JsonClass(generateAdapter = true)
data class ProductData(
    val list: List<TshirtProductElement>? = null
)

@JsonClass(generateAdapter = true)
data class ProductSize(
    val name: String? = null,
    val status: Boolean? = null
)
