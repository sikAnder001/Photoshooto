package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateUserStatusRequest(
    var status: String,
    var reason: List<String>? = null
)
