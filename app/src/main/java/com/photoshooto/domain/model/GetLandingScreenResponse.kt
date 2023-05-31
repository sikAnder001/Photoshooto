package com.photoshooto.domain.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GetLandingScreenResponse(
    val data: GetLandingScreenData? = null,
    val message: String? = null,
    val success: Boolean? = null
)

@JsonClass(generateAdapter = true)
data
class GetLandingScreenData(
    val pic_of_day: List<PicOfDay>? = null,
    val banners: List<Banner>? = null,
    val cameras_on_sales: List<PicOfDay>? = null,
    val top_features: List<TopFeature>? = null,
    val videos: List<VideoItem>? = null,
    val our_achievements: List<OurAchievement>? = null,
    val testimonials: List<Testimonial>? = null

)

@JsonClass(generateAdapter = true)
data class PicOfDay(
    val id: String? = null,
    val file_path: String? = null,
    val title: String? = null
)

@JsonClass(generateAdapter = true)
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
)

@JsonClass(generateAdapter = true)
data class TopFeature(
    val id: String? = null,
    val icon: String? = null,
    val title: String? = null
)

@JsonClass(generateAdapter = true)
data class VideoItem(
    val id: String? = null,
    val title: String? = null,
    val url: String? = null,
    val thumbnail: String? = null,
    val file_path: String? = null,
    val type: String? = null
)


@JsonClass(generateAdapter = true)
data class OurAchievement(
    val id: String? = null,
    val icon: String? = null,
    val hits_number: String? = null,
    val title: String? = null,

    )


@JsonClass(generateAdapter = true)
data class Testimonial(
    val id: String? = null,
    val hits_number: String? = null,
    val userPic: String? = null,
    val subject: String? = null,
    val message: String? = null,
    val title: String? = null,
    val rating: Float? = 0f,
    val user_profile: ProfileDetail? = null
)

@JsonClass(generateAdapter = true)
data class ProfileDetail(
    val name: String? = null,
    val studio_name: String? = null,
    val profile_image: String? = null,
    val profession: String? = null,
)
