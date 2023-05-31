package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetUsersResponse(
    val data: GetUserData? = null,
    val message: String? = null,
    val success: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class GetUserData(
    val list: List<UserElement>? = null,
    val next_page: Int? = null,
    val page: Int? = null
)

@JsonClass(generateAdapter = true)
data class UserProfileDetails(
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
    /*val profession: List<String>? = null,*/
    val profile_image: String? = null,
    val rates: Int? = null,
    val relation: String? = null,
    val studio_name: String? = null
)

@JsonClass(generateAdapter = true)
data class Access(
    val entity: String? = null,
    val permission: String? = null
)

@JsonClass(generateAdapter = true)
data class UserElement(
    val _id: String? = null,
    val access: List<Access>? = null,
    val attachments: List<Attachment>? = null,
    val city_assigned: List<String>? = null,
    val created_at: String? = null,
    val onboarded_at: String? = null,
    val employee_by: String? = null,
    val employee_code: String? = null,
    val id: String? = null,
    val is_2fa_enabled: Boolean? = null,
    val last_active: String? = null,
    val location: Location? = null,
    val module_assigned: List<String>? = null,
    val news_letter_enabled: Boolean? = null,
    val profile_details: UserProfileDetails? = null,
    val reasons: List<String>? = null,
    val refarral_by: String? = null,
    val remarks: String? = null,
    val role: String? = null,
    val status: String? = null,
    val subscriptions_end: String? = null,
    val subscriptions_start: String? = null,
    val type: String? = null,
    val updated_at: String? = null,
    val user_profile: UserProfile? = null,
    val username: String? = null
)

@JsonClass(generateAdapter = true)
data class Attachment(
    val category: String? = null,
    val file_name: String? = null,
    val file_path: String? = null,
    val file_size: Int? = null,
    val file_type: String? = null,
    val id: String? = null,
    val type: String? = null,
    val user_id: String? = null
)
