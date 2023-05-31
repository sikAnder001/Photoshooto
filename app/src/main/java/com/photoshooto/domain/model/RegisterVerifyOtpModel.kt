package com.photoshooto.domain.model

import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RegisterVerifyOtpModel(
    @field:SerializedName("otp")
    var otp: String? = null,

    @field:SerializedName("relation")
    var relation: String? = null,

    @field:SerializedName("type")
    var type: String? = null,

    @field:SerializedName("username")
    var username: String? = null,

    @field:SerializedName("verification_key")
    var verification_key: String? = null,

    @field:SerializedName("password")
    var password: String? = null,

    @field:SerializedName("referral_by")
    var invite_code: String? = null,

    @field:SerializedName("profile_details")
    var profile_details: ProfileDetails? = null,
    @field:SerializedName("news_letter_enabled")
    var news_letter_enabled: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class ProfileDetails(
    @field:SerializedName("mobile")
    var mobile: String? = null,

    @field:SerializedName("first_name")
    var first_name: String? = null,

    @field:SerializedName("name")
    var name: String? = null,

    @field:SerializedName("email")
    var email: String? = null,

    @field:SerializedName("gender")
    var gender: String? = null


)