package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SmsOtpResponse(
    val message: String? = null,
    val mobileOtp: MobileOtp? = null,
    val status: Boolean? = null,
    val success: Boolean = false
)

@JsonClass(generateAdapter = true)
data class MobileOtp(
    val created_at: String? = null,
    val id: Int? = null,
    val mobileNumber: String? = null,
    val otp: Int? = null,
    val updated_at: String? = null
)