package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateQrCodeResponse(
    val data: QrElement? = null,
    val message: String,
    val success: Boolean
)

@JsonClass(generateAdapter = true)
data class GenerateQrData(
    val list: List<QrElement>? = null,
    val next_page: Int? = null,
    val page: Int? = null
)

@JsonClass(generateAdapter = true)
data class QrElement(
    // val _id: String,
    // val created_at: String,
    val expiry: String,
    val id: String,
    // val updated_at: String,
    val url: String,
    val user_id: String
    // val user_profile: UserProfile
)
