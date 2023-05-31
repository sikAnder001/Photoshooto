package com.photoshooto.domain.model

import com.photoshooto.domain.usecase.product_details.AddToCartStandeeElement
import com.photoshooto.domain.usecase.product_details.TshirtElement
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetCartDetailsResponse(
    val data: CartDetailsData? = null,
    val message: String,
    val success: Boolean
)

@JsonClass(generateAdapter = true)
data class CartDetailsData(
    val list: List<CartDetails>? = null,
    val next_page: Int,
    val page: Int
)

@JsonClass(generateAdapter = true)
data class CartDetails(
    val _id: String? = null,
    val created_at: String? = null,
    val tshirt_list: List<TshirtElement>? = null,
    val standee_list: List<AddToCartStandeeElement>? = null,
    val updated_at: String? = null,
    val user_id: String? = null,
    val user_profile: UserProfile? = null
)

@JsonClass(generateAdapter = true)
data class UserProfile(
    val _id: String? = null,
    val address: String? = null,
    val alt_email: String? = null,
    val alt_mobile: String? = null,
    val background_image: String? = null,
    val birth_date: String? = null,
    val city: String? = null,
    val email: String? = null,
    val equipment_use: String? = null,
    val experience: Int? = null,
    val flat_no: String? = null,
    val gender: String? = null,
    val gps_location: GpsLocation? = null,
    val landmark: String? = null,
    val language_know: List<String>? = null,
    val mobile: String? = null,
    val my_self: String? = null,
    val name: String? = null,
    val pincode: String? = null,
    val online_status: String? = null,
    val profession: String? = null,
    val profile_image: String? = null,
    val rates: Int? = null,
    val rating: Double? = 0.0,
    val relation: String? = null,
    val state: String? = null,
    val studio_name: String? = null,
    val subscriptions_end: String? = null,
    val subscriptions_start: String? = null
)
