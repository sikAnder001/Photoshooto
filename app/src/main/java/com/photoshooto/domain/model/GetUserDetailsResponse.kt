package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetUserDetailsResponse(
    val data: UserDetailsData? = null,
    val message: String? = null,
    val success: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class UserDetailsData(
    val _id: String? = null,
    val access: List<Access>? = null,
    val attachments: List<Attachment>? = null,
    val city_assigned: List<String>? = null,
    val created_at: String? = null,
    val employee_by: String? = null,
    val employee_code: String? = null,
    val id: String? = null,
    val is_2fa_enabled: Boolean? = null,
    val last_active: String? = null,
    val location: Location? = null,
    val module_assigned: List<String>? = null,
    val news_letter_enabled: Boolean? = null,
    val profile_details: GetProfileDetails? = null,
    val reasons: List<String>? = null,
    val refarral_by: String? = null,
    val remarks: String? = null,
    val role: String? = null,
    val status: String? = null,
    val subscriptions_end: String? = null,
    val subscriptions_start: String? = null,
    val type: String? = null,
    val updated_at: String? = null,
    val username: String? = null
)

/*@JsonClass(generateAdapter = true)
data class Access(
    val entity: String? = null,
    val permission: String? = null
)*/

/*@JsonClass(generateAdapter = true)
data class GpsLocation(
    val _id: String? = null,
    val latitude: Int? = null,
    val longitude: Int? = null
)*/

/*@JsonClass(generateAdapter = true)
data class Location(
    val _id: String? = null,
    val address: String? = null,
    val city: String? = null,
    val flat_no: String? = null,
    val gps_location: GpsLocation? = null,
    val landmark: String? = null,
    val state: String? = null
)*/

/*@JsonClass(generateAdapter = true)
data class ProfileDetailss(
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
    val rates: Int? = null,
    val relation: String? = null,
    val studio_name: String? = null
)*/
