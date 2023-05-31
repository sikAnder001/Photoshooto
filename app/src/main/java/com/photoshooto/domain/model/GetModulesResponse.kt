package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetModulesResponse(
    val data: ModuleData? = null,
    val message: String? = null,
    val success: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class ModuleData(
    val list: List<ModuleElement>? = null,
    val next_page: Int? = null,
    val page: Int? = null
)

@JsonClass(generateAdapter = true)
data class ModuleElement(
    val _id: String? = null,
    val created_at: String? = null,
    val id: String? = null,
    val name: String? = null,
    val updated_at: String? = null,
    val user_profile: UserProfile? = null
)
