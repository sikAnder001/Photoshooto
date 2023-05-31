package com.photoshooto.domain.usecase.product_details

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddTshirtToCartRequest(
    val tshirt_list: List<TshirtElement>? = null,
    val standee_list: List<AddToCartStandeeElement>? = null
)

@JsonClass(generateAdapter = true)
data class AddToCartStandeeElement(
    val alternate_contact_number: String,
    val contact_number: String,
    val description: String,
    val discount: Double,
    val height: String,
    val id: String,
    val images: List<String>? = null,
    val photographer_id: String,
    val price: Double,
    var quantity: Int,
    val studio_address: String,
    val studio_logo: List<String>? = null,
    val studio_name: String? = null,
    val studio_tagline: String? = null,
    val type: String,
    val weight: String,
    val width: String
)

@JsonClass(generateAdapter = true)
data class TshirtSize(
    val is_avilable: Boolean,
    var quantity: Int,
    val size: String
)

@JsonClass(generateAdapter = true)
data class TshirtElement(
    val color: String,
    val description: String,
    val discount: Int,
    val id: String,
    val images: List<String>? = null,
    val price: Int,
    val properties: List<String>? = null,
    val sizes: List<TshirtSize>? = null,
    val studio_logo: List<String>? = null,
    val studio_name: String? = null,
    val studio_tagline: String? = null
)
