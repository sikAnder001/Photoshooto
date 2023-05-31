package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserProfileModel(
    val data: UserData? = null,
    val message: String? = null,
    val success: Boolean
)

@JsonClass(generateAdapter = true)
data class UserData(
    val token: String? = null,
    val user: User? = null
)

@JsonClass(generateAdapter = true)
data class User(
    val access: List<Acces>? = null,
    val attachments: List<Any>? = null,
    val city_assigned: List<Any>? = null,
    val created_at: String? = null,
    val employee_by: String? = null,
    val employee_code: String? = null,
    val id: String? = null,
    val is_2fa_enabled: Boolean,
    val is_3rd_party_login: String,
    val last_active: Any,
    val location: Location? = null,
    val module_assigned: List<Any>? = null,
    val news_letter_enabled: Boolean,
    val onboarded_at: Any,
    val online_status: String,
    val profile_complete: Double? = 0.0,
    val profile_details: ProfileDetailsData? = null,
    val reasons: List<Any>,
    val refarral_by: String? = null,
    val remarks: String,
    val reporting_person: String,
    val role: String? = null,
    val status: String? = null,
    val subscriptions_id: String? = null,
    val subscriptions_end: String? = null,
    val subscriptions_start: String? = null,
    val updated_at: String? = null,
    val type: String? = null,
    val username: String? = null,
)

@JsonClass(generateAdapter = true)
data class ProfileDetailsData(
    val _id: String? = null,
    val alt_email: String? = null,
    val alt_mobile: String? = null,
    val background_image: String? = null,
    val birth_date: String? = null,
    val email: String? = null,
    val equipment_use: String? = null,
    val experience: Int? = null,
    val gender: String? = null,
    val language_know: List<Any>,
    val mobile: String? = null,
    val my_self: String? = null,
    val name: String? = null,
    val profession: String? = null,
    val profile_image: String? = null,
    val rates: Int,
    val studio_name: String? = null
)

