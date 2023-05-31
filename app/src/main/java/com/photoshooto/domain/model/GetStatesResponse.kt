package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetStatesResponse(
    val data: List<StatesData>? = null,
    val message: String? = null,
    val success: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class StatesData(
    val id: Int? = null,
    val name: String? = null
)
