package com.photoshooto.domain.usecase.service

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServiceValidRequest(
    val number: String = "",
    val latitude: String = "",
    val longitude: String = ""
)
