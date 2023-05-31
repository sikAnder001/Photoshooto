package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetProfileModel(
    val data: ProfileData,
    val message: String? = null,
    val success: Boolean
)

@JsonClass(generateAdapter = true)
data class ProfileData(
    val _id: String? = null,
    val access: List<Acces>? = null,
    val attachments: List<FileUpload>? = null,
    val created_at: String? = null,
    val id: String? = null,
    val is_2fa_enabled: Boolean,
    val location: Location? = null,
    val news_letter_enabled: Boolean,
    val profile_details: GetProfileDetails? = null,
    val refarral_by: String? = null,
    val role: String? = null,
    val status: String? = null,
    val subscriptions_end: String? = null,
    val subscriptions_start: String? = null,
    val type: String? = null,
    val updated_at: String? = null,
    val username: String? = null,
    val profile_complete: Double? = 0.0,
    val referral_code: String? = ""
)

@JsonClass(generateAdapter = true)
data class Acces(
    val entity: String? = null,
    val permission: String? = null
)


@JsonClass(generateAdapter = true)
data class Location(
    val _id: String? = null,
    val address: String? = null,
    val city: String? = null,
    val flat_no: String? = null,
    val gps_location: GpsLocation,
    val landmark: String? = null,
    val state: String? = null,
    val pincode: String? = null

)

@JsonClass(generateAdapter = true)
data class GetProfileDetails(
    val _id: String? = null,
    val alt_email: String? = null,
    val alt_mobile: String? = null,
    val background_image: String? = null,
    val birth_date: String? = null,
    val email: String? = null,
    val equipment_use: String? = null,
    val experience: Int? = null,
    val gender: String? = null,
    val language_know: List<String>? = null,
    val mobile: String? = null,
    val my_self: String? = null,
    val name: String? = null,
    val profession: String? = null,
    val profile_image: String? = null,
    val rates: Int,
    val relation: String? = null,
    val studio_name: String? = null
)

@JsonClass(generateAdapter = true)
data class GpsLocation(
    val _id: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
)


