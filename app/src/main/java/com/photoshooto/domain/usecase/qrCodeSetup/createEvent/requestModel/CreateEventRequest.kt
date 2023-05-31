package com.photoshooto.domain.usecase.qrCodeSetup.createEvent.requestModel

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize


@Parcelize
@JsonClass(generateAdapter = true)
data class CreateEventRequest(
    var project_id: String? = "",
    var event_name: String? = "",
    var event_type: String? = "",
    var event_start_date: String? = "",
    var event_end_date: String? = "",
    var event_start_time: String? = "",
    var event_end_time: String? = "",
    var relation: String? = "",
    var location: String? = "",
    var standee_type: String? = "",
    var qrcode_id: String? = "",
    var no_users: String? = "",
    var client_name: String? = "",
    var client_contact_number: String? = "",
    var quantity: String? = "",
) : Parcelable

