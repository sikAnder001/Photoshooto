package com.photoshooto.domain.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ReviewModel(
    @SerializedName("data")
    @Expose
    val data: ReviewData?,
    @SerializedName("message")
    @Expose
    val message: String?,
    @SerializedName("success")
    @Expose
    val success: Boolean?
) {
    @JsonClass(generateAdapter = true)
    data class ReviewData(
        @SerializedName("list")
        @Expose
        val list: List<ReviewList>?,
        @SerializedName("next_page")
        @Expose
        val nextPage: Int?,
        @SerializedName("page")
        @Expose
        val page: Int?
    ) {
        @JsonClass(generateAdapter = true)
        data class ReviewList(
            @SerializedName("attachments")
            @Expose
            val attachments: List<Any?>?,
            @SerializedName("created_at")
            @Expose
            val createdAt: String?,
            @SerializedName("dislikes")
            @Expose
            val dislikes: Int?,
            @SerializedName("_id")
            @Expose
            val _id: String? = null,
            @SerializedName("id")
            @Expose
            val id: String? = null,
            @SerializedName("likes")
            @Expose
            val likes: Int?,
            @SerializedName("message")
            @Expose
            val message: String?,
            @SerializedName("order_id")
            @Expose
            val orderId: String?,
            @SerializedName("project_id")
            @Expose
            val projectId: String?,
            @SerializedName("rating")
            @Expose
            val rating: Int?,
            @SerializedName("updated_at")
            @Expose
            val updatedAt: String?,
            @SerializedName("user_id")
            @Expose
            val userId: String?,
            @SerializedName("user_profile")
            @Expose
            val userProfile: UserProfile?
        ) {
            @JsonClass(generateAdapter = true)
            data class UserProfile(
                @SerializedName("address")
                @Expose
                val address: String?,
                @SerializedName("alt_email")
                @Expose
                val altEmail: String?,
                @SerializedName("alt_mobile")
                @Expose
                val altMobile: String?,
                @SerializedName("background_image")
                @Expose
                val backgroundImage: String?,
                @SerializedName("birth_date")
                @Expose
                val birthDate: String?,
                @SerializedName("city")
                @Expose
                val city: String?,
                @SerializedName("email")
                @Expose
                val email: String?,
                @SerializedName("equipment_use")
                @Expose
                val equipmentUse: String?,
                @SerializedName("experience")
                @Expose
                val experience: Int?,
                @SerializedName("flat_no")
                @Expose
                val flatNo: String?,
                @SerializedName("gender")
                @Expose
                val gender: String?,
                @SerializedName("gps_location")
                @Expose
                val gpsLocation: GpsLocation?,
                @SerializedName("_id")
                @Expose
                val id: String?,
                @SerializedName("landmark")
                @Expose
                val landmark: String?,
                @SerializedName("language_know")
                @Expose
                val languageKnow: List<Any?>?,
                @SerializedName("mobile")
                @Expose
                val mobile: String?,
                @SerializedName("my_self")
                @Expose
                val mySelf: String?,
                @SerializedName("name")
                @Expose
                val name: String?,
                @SerializedName("profession")
                @Expose
                val profession: String?,
                @SerializedName("profile_image")
                @Expose
                val profileImage: String?,
                @SerializedName("rates")
                @Expose
                val rates: Int?,
                @SerializedName("relation")
                @Expose
                val relation: String?,
                @SerializedName("state")
                @Expose
                val state: String?,
                @SerializedName("studio_name")
                @Expose
                val studioName: String?
            ) {
                @JsonClass(generateAdapter = true)
                data class GpsLocation(
                    @SerializedName("_id")
                    @Expose
                    val id: String?,
                    @SerializedName("latitude")
                    @Expose
                    val latitude: Int?,
                    @SerializedName("longitude")
                    @Expose
                    val longitude: Int?
                )
            }
        }
    }
}