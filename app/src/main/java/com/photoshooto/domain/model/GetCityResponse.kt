package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetCityResponse(
    val data: List<CityData>? = null,
    val message: String? = null,
    val success: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class CityData(
    val id: Int? = null,
    val latitude: String? = null,
    val longitude: String? = null,
    val name: String? = "",
    val tier_type: Int? = 1,
    val icon: String? = null,
    val image: String? = null,
) {
    override fun toString(): String {
        return name!!
    }
}
