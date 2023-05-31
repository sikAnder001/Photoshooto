package com.photoshooto.domain.usecase.reset_pwd

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class PwdChangeRequestModel(
    var password: String? = null
)

