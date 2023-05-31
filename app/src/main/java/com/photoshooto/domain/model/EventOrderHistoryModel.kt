package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EventOrderHistoryModel(
    val data: EventData,
    val success: Boolean = false,
    val message: String = ""
)

@JsonClass(generateAdapter = true)
data class EventData(
    val next_page: Int = 0,
    val page: Int = 0,
    val list: List<EventOrderHistoryElement>?
)

@JsonClass(generateAdapter = true)
data class EventOrderHistoryElement(
    val _id: String,
    val client_contact_number: String,
    val client_name: String,
    val event_end_date: String,
    val event_end_time: String,
    val event_name: String,
    val event_start_date: String,
    val event_start_time: String,
    val event_type: String,
    val id: String,
    val location: String,
    val no_users: Int,
    val project_id: String,
    val qrcode_id: String,
    val quantity: Int,
    val standee_type: String,
    val user_id: String,
    val created_at: String,
    val updated_at: String,
    val qrcode: List<EventQRCode>,
    val user_profile: EventUserProfile

)


@JsonClass(generateAdapter = true)
data class EventUserProfile(
    val _id: String? = null,
    val address: String? = null,
    val alt_email: String? = null,
    val alt_mobile: String? = null,
    val background_image: String,
    val birth_date: String,
    val city: String,
    val email: String,
    val equipment_use: String,
    val experiance: Int,
    val flat_no: String,
    val gender: String,
    val gps_location: EventGpsLocation,
    val landmark: String,
    val language_know: List<Any>,
    val mobile: Long,
    val my_self: String,
    val name: String,
    val pincode: Int,
    val profession: String,
    val profile_image: String,
    val rates: Int,
    val state: String,
    val studio_name: String
)

@JsonClass(generateAdapter = true)
data class EventQRCode(
    val _id: String,
    val expiry: String,
    val id: String,
    val url: String,
    val user_id: String,
    val __v: Int,
    val created_at: String,
    val updated_at: String
)

@JsonClass(generateAdapter = true)
data class EventGpsLocation(
    val _id: String? = null,
    val latitude: Int,
    val longitude: Int
)
