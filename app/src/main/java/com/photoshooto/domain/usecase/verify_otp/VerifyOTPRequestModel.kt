package com.photoshooto.domain.usecase.verify_otp

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class VerifyOTPRequestModel(
    var verification_key: String? = null,
    var otp: String? = null
)

