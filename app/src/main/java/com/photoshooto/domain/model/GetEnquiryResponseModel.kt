package com.photoshooto.domain.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetEnquiryResponseModel(
    @SerializedName("data")
    @Expose
    val `data`: Data?,
    @SerializedName("message")
    @Expose
    val message: String?,
    @SerializedName("success")
    @Expose
    val success: Boolean?
) {
    data class Data(
        @SerializedName("list")
        @Expose
        val list: List<ListData>?,
        @SerializedName("next_page")
        @Expose
        val nextPage: Int?,
        @SerializedName("page")
        @Expose
        val page: Int?
    ) {
        data class ListData(
            @SerializedName("budget")
            @Expose
            val budget: String?,
            @SerializedName("created_at")
            @Expose
            val createdAt: String?,
            @SerializedName("end_date")
            @Expose
            val endDate: String?,
            @SerializedName("end_time")
            @Expose
            val endTime: String?,
            @SerializedName("event_type")
            @Expose
            val eventType: List<String?>?,
            @SerializedName("_id")
            @Expose
            val _id: String?,
            @SerializedName("id")
            @Expose
            val id: String?,
            @SerializedName("locality")
            @Expose
            val locality: String?,
            @SerializedName("location")
            @Expose
            val location: String?,
            @SerializedName("mobile")
            @Expose
            val mobile: String?,
            @SerializedName("name")
            @Expose
            val name: String?,
            @SerializedName("photographer_id")
            @Expose
            val photographerId: String?,
            @SerializedName("replied")
            @Expose
            val replied: String?,
            @SerializedName("start_date")
            @Expose
            val startDate: String?,
            @SerializedName("start_time")
            @Expose
            val startTime: String?,
            @SerializedName("status")
            @Expose
            val status: String?,
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