package com.photoshooto.domain.usecase.service

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendSmsRequest(
    val mobileNumber: String = ""
)

