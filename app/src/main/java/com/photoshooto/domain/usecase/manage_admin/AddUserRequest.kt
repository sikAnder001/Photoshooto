package com.photoshooto.domain.usecase.manage_admin

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AddUserRequest(
    val attachments: List<Attachment>? = null,
    val location: Location? = null,
    val password: String? = null,
    val profile_details: ProfileDetails? = null,
    val type: String? = null,
    val role: String? = null,
    val employee_code: String? = null,
    val employee_by: String? = null
)

@JsonClass(generateAdapter = true)
data class UpdateUserRequest(
    val status: String? = "",
)

@JsonClass(generateAdapter = true)
data class Attachment(
    val file_name: String? = null,
    val file_path: String? = null,
    val file_type: String? = null,
    val id: String? = null
)

@JsonClass(generateAdapter = true)
data class Location(
    val address: String? = null,
    val city: String? = null,
    val landmark: String? = null,
    val pincode: Int? = null,
    val state: String? = null
)

@JsonClass(generateAdapter = true)
data class ProfileDetails(
    val backgroundImage: String? = null,
    val birth_date: String? = null,
    val alt_email: String? = null,
    val email: String? = null,
    val gender: String? = null,
    val mobile: String? = null,
    val name: String? = null,
    val profileImage: String? = null,
    val studio_name: String? = null,
    val language_know: List<String>? = null,
    val module_assigned: List<String>? = null,
    val city_assigned: List<String>? = null
)
