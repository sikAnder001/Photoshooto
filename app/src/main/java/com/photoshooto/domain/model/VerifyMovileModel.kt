package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VerifyMovileModel(
    val data: Data? = null,
    val message: String? = null,
    val success: Boolean
)

@JsonClass(generateAdapter = true)
data class Data(
    val otp: String? = null,
    var verification_key: String? = null,
    val token: String? = null,
    val user: User? = null
)