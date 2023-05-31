package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetAddressResponse(
    val data: AddressDetailsData? = null,
    val message: String,
    val success: Boolean
)

@JsonClass(generateAdapter = true)
data class AddressDetailsData(
    val list: List<AddressElement>? = null,
    val next_page: Int,
    val page: Int
)

@JsonClass(generateAdapter = true)
data class AddressElement(
    val _id: String,
    val address: String? = null,
    val city: String? = null,
    val created_at: String? = null,
    val flat_no: String? = null,
    val gps_location: GpsLocation? = null,
    val id: String? = null,
    val is_default: Boolean = true,
    val landmark: String? = null,
    val pincode: String? = null,
    val state: String? = null,
    val type: String? = null,
    val updated_at: String? = null,
    val user_id: String? = null,
    val user_profile: UserProfile? = null
)
