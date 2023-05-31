package com.photoshooto.domain.usecase.login

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserLoginRequest(
    var username: String? = null,
    var password: String? = null
)
