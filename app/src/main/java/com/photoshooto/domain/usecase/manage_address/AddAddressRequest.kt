package com.photoshooto.domain.usecase.manage_address

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddAddressRequest(
    val address: String? = null,
    val city: String? = null,
    val flat_no: String? = null,
    val is_default: Boolean? = null,
    val landmark: String? = null,
    val latitude: Long? = null,
    val longitude: Long? = null,
    val pincode: String? = null,
    val state: String? = null,
    val type: String? = null
)