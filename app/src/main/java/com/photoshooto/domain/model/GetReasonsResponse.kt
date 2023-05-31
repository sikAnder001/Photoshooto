package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetReasonsResponse(
    val data: ReasonData? = null,
    val message: String? = null,
    val success: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class ReasonData(
    val list: List<ReasonElement>? = null,
    val next_page: Int? = null,
    val page: Int? = null
)

@JsonClass(generateAdapter = true)
data class ReasonElement(
    val _id: String? = null,
    val created_at: String? = null,
    val id: String? = null,
    val reason_name: String? = null,
    val type: String? = null,
    val updated_at: String? = null,
    val user_profile: UserProfile? = null,
    var is_selected: Boolean = false
)
