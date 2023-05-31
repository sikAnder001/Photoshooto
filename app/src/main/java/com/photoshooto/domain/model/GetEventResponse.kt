package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetEventResponse(
    var success: Boolean? = false,
    var data: GetEventData? = GetEventData(),
    var message: String? = ""
) {
    @JsonClass(generateAdapter = true)
    data class GetEventData(
        var list: List<EventModel?>? = listOf(),
        var page: Int? = 0,
        var next_page: Int? = 0
    ) {
        @JsonClass(generateAdapter = true)
        data class EventModel(
            var _id: String? = "",
            var client_contact_number: String? = "",
            var client_name: String? = "",
            var event_end_date: String? = "",
            var event_end_time: String? = "",
            var event_name: String? = "",
            var event_start_date: String? = "",
            var event_start_time: String? = "",
            var event_type: String? = "",
            var id: String? = "",
            var location: String? = "",
            var no_users: String? = "",
            var project_id: String? = "",
            var qrcode_id: String? = "",
            var quantity: String? = "",
            var standee_type: String? = "",
            var user_id: String? = "",
            var created_at: String? = "",
            var updated_at: String? = "",
            var qrcode: List<Qrcode?>? = listOf(),
        ) {
            @JsonClass(generateAdapter = true)
            data class Qrcode(
                var _id: String? = "",
                var expiry: String? = "",
                var id: String? = "",
                var url: String? = "",
                var user_id: String? = "",
                var __v: String? = "",
                var created_at: String? = "",
                var updated_at: String? = ""
            )
        }
    }
}