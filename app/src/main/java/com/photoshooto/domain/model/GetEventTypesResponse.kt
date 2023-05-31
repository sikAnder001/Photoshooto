package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetEventTypesResponse(
    var success: Boolean? = false,
    var data: GetEventTypesData? = GetEventTypesData(),
    var message: String? = ""
) {
    @JsonClass(generateAdapter = true)
    data class GetEventTypesData(
        var list: List<GetEventTypesModel>? = listOf(),
        var page: String? = "",
        var next_page: String? = ""
    ) {
        @JsonClass(generateAdapter = true)
        data class GetEventTypesModel(
            var _id: String? = "",
            var id: String? = "",
            var type: String? = "",
            var created_at: String? = "",
            var updated_at: String? = ""
        )
    }
}