package com.photoshooto.domain.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserDashboardModel(
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
        @SerializedName("banners")
        @Expose
        val banners: List<Banner?>?,
        @SerializedName("testimonials")
        @Expose
        val testimonials: List<Testimonial?>?,
        @SerializedName("top_cities")
        @Expose
        val topCities: List<TopCity?>?,
        @SerializedName("top_photography_services")
        @Expose
        val topPhotographyServices: List<PhotographerService?>?,
        @SerializedName("top_features")
        @Expose
        val topFeatures: List<TopFeature?>?,
        @SerializedName("top_photographer")
        @Expose
        val topPhotographer: List<TopPhotographer?>?,
        @SerializedName("videos")
        @Expose
        val videos: List<Video?>?
    ) {
        data class Banner(
            @SerializedName("background_image")
            @Expose
            val background_image: String?,
            @SerializedName("code")
            @Expose
            val code: String?,
            @SerializedName("description")
            @Expose
            val description: String?,
            @SerializedName("id")
            @Expose
            val id: String?,
            @SerializedName("offer_price")
            @Expose
            val offer_price: Double?,
            @SerializedName("offer_type")
            @Expose
            val offer_type: String?,
            @SerializedName("price")
            @Expose
            val price: Double?,
            @SerializedName("subtitle")
            @Expose
            val subtitle: String?,
            @SerializedName("title")
            @Expose
            val title: String?,
            @SerializedName("user_type")
            @Expose
            val user_type: String?
        ) {
            data class UserProfile(
                @SerializedName("address")
                @Expose
                val address: String?,
                @SerializedName("birth_date")
                @Expose
                val birthDate: String?,
                @SerializedName("city")
                @Expose
                val city: String?,
                @SerializedName("email")
                @Expose
                val email: String?,
                @SerializedName("experience")
                @Expose
                val experience: Int?,
                @SerializedName("gender")
                @Expose
                val gender: String?,
                @SerializedName("gps_location")
                @Expose
                val gpsLocation: GpsLocation?,
                @SerializedName("mobile")
                @Expose
                val mobile: String?,
                @SerializedName("name")
                @Expose
                val name: String?,
                @SerializedName("pincode")
                @Expose
                val pincode: Int?,
                @SerializedName("profession")
                @Expose
                val profession: String?,
                @SerializedName("rates")
                @Expose
                val rates: Int?,
                @SerializedName("state")
                @Expose
                val state: String?
            ) {
                data class GpsLocation(
                    @SerializedName("latitude")
                    @Expose
                    val latitude: Int?,
                    @SerializedName("longitude")
                    @Expose
                    val longitude: Int?
                )
            }
        }

        data class Testimonial(
            @SerializedName("dislikes")
            @Expose
            val dislikes: Int?,
            @SerializedName("id")
            @Expose
            val id: String?,
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
            @SerializedName("user_id")
            @Expose
            val userId: String?,
            @SerializedName("user_profile")
            @Expose
            val userProfile: UserProfile?
        ) {
            data class UserProfile(
                @SerializedName("background_image")
                @Expose
                val backgroundImage: String?,
                @SerializedName("birth_date")
                @Expose
                val birthDate: String?,
                @SerializedName("email")
                @Expose
                val email: String?,
                @SerializedName("equipment_use")
                @Expose
                val equipmentUse: String?,
                @SerializedName("experience")
                @Expose
                val experience: Int?,
                @SerializedName("gender")
                @Expose
                val gender: String?,
                @SerializedName("gps_location")
                @Expose
                val gpsLocation: GpsLocation?,
                @SerializedName("mobile")
                @Expose
                val mobile: String?,
                @SerializedName("name")
                @Expose
                val name: String?,
                @SerializedName("profession")
                @Expose
                val profession: String?,
                @SerializedName("rates")
                @Expose
                val rates: Int?,
                @SerializedName("studio_name")
                @Expose
                val studioName: String?
            ) {
                data class GpsLocation(
                    @SerializedName("latitude")
                    @Expose
                    val latitude: Int?,
                    @SerializedName("longitude")
                    @Expose
                    val longitude: Int?
                )
            }
        }

        data class TopCity(
            @SerializedName("image")
            @Expose
            val image: String?,
            @SerializedName("icon")
            @Expose
            val icon: String?,
            @SerializedName("tier_type")
            @Expose
            val tier_type: String?,
            @SerializedName("name")
            @Expose
            val name: String?
        )

        data class PhotographerService(
            @SerializedName("id")
            @Expose
            val id: String?,
            @SerializedName("type")
            @Expose
            val type: String?,
            @SerializedName("icon")
            @Expose
            val icon: String?
        )

        data class TopFeature(
            @SerializedName("id")
            @Expose
            val id: String?,
            @SerializedName("title")
            @Expose
            val title: String?
        )

        data class TopPhotographer(
            @SerializedName("access")
            @Expose
            val access: List<Acces?>?,
            @SerializedName("attachments")
            @Expose
            val attachments: List<Attachment?>?,
            @SerializedName("employee_by")
            @Expose
            val employeeBy: String?,
            @SerializedName("employee_code")
            @Expose
            val employeeCode: String?,
            @SerializedName("id")
            @Expose
            val id: String?,
            @SerializedName("is_2fa_enabled")
            @Expose
            val is2faEnabled: Boolean?,
            @SerializedName("location")
            @Expose
            val location: Location?,
            @SerializedName("news_letter_enabled")
            @Expose
            val newsLetterEnabled: Boolean?,
            @SerializedName("online_status")
            @Expose
            val onlineStatus: String?,
            @SerializedName("password")
            @Expose
            val password: String?,
            @SerializedName("profile_details")
            @Expose
            val profileDetails: ProfileDetails?,
            @SerializedName("role")
            @Expose
            val role: String?,
            @SerializedName("status")
            @Expose
            val status: String?,
            @SerializedName("type")
            @Expose
            val type: String?,
            @SerializedName("username")
            @Expose
            val username: String?,
            @SerializedName("__v")
            @Expose
            val v: Int?
        ) {
            data class Access(
                @SerializedName("entity")
                @Expose
                val entity: String?,
                @SerializedName("permission")
                @Expose
                val permission: String?
            )

            data class Attachment(
                @SerializedName("category")
                @Expose
                val category: String?,
                @SerializedName("file_name")
                @Expose
                val fileName: String?,
                @SerializedName("file_path")
                @Expose
                val filePath: String?,
                @SerializedName("file_size")
                @Expose
                val fileSize: Int?,
                @SerializedName("file_type")
                @Expose
                val fileType: String?,
                @SerializedName("id")
                @Expose
                val id: String?,
                @SerializedName("type")
                @Expose
                val type: String?,
                @SerializedName("user_id")
                @Expose
                val userId: String?
            )

            data class Location(
                @SerializedName("gps_location")
                @Expose
                val gpsLocation: GpsLocation?
            ) {
                data class GpsLocation(
                    @SerializedName("latitude")
                    @Expose
                    val latitude: Int?,
                    @SerializedName("longitude")
                    @Expose
                    val longitude: Int?
                )
            }

            data class ProfileDetails(
                @SerializedName("alt_email")
                @Expose
                val altEmail: String?,
                @SerializedName("background_image")
                @Expose
                val backgroundImage: String?,
                @SerializedName("profile_image")
                @Expose
                val profile_image: String?,
                @SerializedName("birth_date")
                @Expose
                val birthDate: String?,
                @SerializedName("email")
                @Expose
                val email: String?,
                @SerializedName("equipment_use")
                @Expose
                val equipmentUse: String?,
                @SerializedName("experience")
                @Expose
                val experience: Int?,
                @SerializedName("gender")
                @Expose
                val gender: String?,
                @SerializedName("mobile")
                @Expose
                val mobile: String?,
                @SerializedName("name")
                @Expose
                val name: String?,
                @SerializedName("profession")
                @Expose
                val profession: String?,
                @SerializedName("rates")
                @Expose
                val rates: Float?,
                @SerializedName("studio_name")
                @Expose
                val studioName: String?
            )
        }

        data class Video(
            @SerializedName("category")
            @Expose
            val category: String?,
            @SerializedName("file_height")
            @Expose
            val fileHeight: String?,
            @SerializedName("file_name")
            @Expose
            val fileName: String?,
            @SerializedName("file_path")
            @Expose
            val filePath: String?,
            @SerializedName("file_size")
            @Expose
            val fileSize: Int?,
            @SerializedName("file_type")
            @Expose
            val fileType: String?,
            @SerializedName("file_width")
            @Expose
            val fileWidth: String?,
            @SerializedName("id")
            @Expose
            val id: String?,
            @SerializedName("is_deleted")
            @Expose
            val isDeleted: Boolean?,
            @SerializedName("status")
            @Expose
            val status: String?,
            @SerializedName("type")
            @Expose
            val type: String?,
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
                @SerializedName("birth_date")
                @Expose
                val birthDate: String?,
                @SerializedName("city")
                @Expose
                val city: String?,
                @SerializedName("email")
                @Expose
                val email: String?,
                @SerializedName("experience")
                @Expose
                val experience: Int?,
                @SerializedName("gender")
                @Expose
                val gender: String?,
                @SerializedName("gps_location")
                @Expose
                val gpsLocation: GpsLocation?,
                @SerializedName("mobile")
                @Expose
                val mobile: String?,
                @SerializedName("name")
                @Expose
                val name: String?,
                @SerializedName("pincode")
                @Expose
                val pincode: Int?,
                @SerializedName("profession")
                @Expose
                val profession: String?,
                @SerializedName("rates")
                @Expose
                val rates: Int?,
                @SerializedName("state")
                @Expose
                val state: String?
            ) {
                data class GpsLocation(
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