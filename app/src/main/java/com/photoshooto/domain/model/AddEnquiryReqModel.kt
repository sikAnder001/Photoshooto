package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AddEnquiryReqModel(
    var name: String = "",
    var mobile: String = "",
    var location: String = "",
    var locality: String = "",
    var event_type: ArrayList<String>? = null,
    var budget: String = "",
    var start_date: String = "",
    var end_date: String = "",
    var start_time: String = "",
    var end_time: String = "",
    var photographer_id: String = "",
)