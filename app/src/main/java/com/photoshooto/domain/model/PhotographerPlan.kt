package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PhotographerPlanResponse(
    val data: List<PlanDetails>,
    val message: String,
    val success: Boolean
) {
    @JsonClass(generateAdapter = true)
    data class PlanDetails(
        val icon: String? = null,
        val _id: String? = null,
        val id: String? = null,
        val amount: String? = null,
        val session_hours: String? = null,
        val type: String? = null,
        val user_id: String? = null,
        val created_at: String? = null,
        val updated_at: String? = null
    )
}

@JsonClass(generateAdapter = true)
data class PhotographerPlanBody(
    val type: String? = null,
    val amount: String? = null,
    val session_hours: String? = null,
    val icon: String? = null,
)