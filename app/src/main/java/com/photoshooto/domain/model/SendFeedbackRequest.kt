package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendFeedbackRequest(
    var rating: Int? = 0,
    var subject: String? = null,
    var message: String? = null,
    var order_id: String? = null,
    var attachments: ArrayList<String>,
    var improve_list: ArrayList<String>,
    var project_id: String? = null,
    var user_id: String? = null,
    var response: Boolean? = false,
    var quality: Boolean? = false,
    var delivery: Boolean? = false,
)