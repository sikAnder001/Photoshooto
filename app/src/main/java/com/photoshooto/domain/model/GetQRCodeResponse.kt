package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetQRCodeResponse(
    var success: Boolean? = false,
    var data: GetQRCodeData? = GetQRCodeData(),
    var message: String? = ""
) {
    @JsonClass(generateAdapter = true)
    data class GetQRCodeData(
        var list: List<GetQRCodeModel>? = listOf(),
        var page: Int? = 0,
        var next_page: Int? = 0
    ) {
        @JsonClass(generateAdapter = true)
        data class GetQRCodeModel(
            var _id: String? = "",
            var expiry: String? = "",
            var id: String? = "",
            var url: String? = "",
            var user_id: String? = "",
            var created_at: String? = "",
            var updated_at: String? = ""
        )
    }
}