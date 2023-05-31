package com.photoshooto.domain.usecase.notificationApi

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class NotificationRequestModel(
    var channel: Array<String>? = null,
    var message: String? = null,
    var notify_user_id: String? = null,
    var status: String? = null,
    var type: String? = null
)