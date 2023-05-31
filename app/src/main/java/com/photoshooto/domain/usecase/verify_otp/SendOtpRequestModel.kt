package com.photoshooto.domain.usecase.verify_otp

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class SendOtpRequestModel(
    var username: String? = null,
    var type: String? = null
)

