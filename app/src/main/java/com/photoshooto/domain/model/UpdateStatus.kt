package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateStatus(
    var status: String? = null,
    var reason: List<String>? = null
)
