package com.photoshooto.ui.notification.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

@JsonClass(generateAdapter = true)
@Parcelize
data class NotificationModel(

    @SerializedName("data")
    var data: NotiData,
    @SerializedName("message")
    var message: String,
    @SerializedName("success")
    var success: Boolean
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class NotiData(
    @SerializedName("list")
    var list: List<NotificationItem>,
    @SerializedName("next_page")
    var nextPage: Int?,
    @SerializedName("page")
    var page: Int?
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class NotificationItem(
    @SerializedName("channel")
    var channel: List<String>,
    @SerializedName("created_at")
    var createdAt: String,
    @SerializedName("_id")
    var _id: String,
    @SerializedName("id")
    var id: String,
    @SerializedName("message")
    var message: String,
    @SerializedName("notify_user_id")
    var notifyUserId: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("type")
    var type: String,
    @SerializedName("updated_at")
    var updatedAt: String,
    @SerializedName("user_id")
    var userId: String,
    @SerializedName("user_profile")
    var userProfile: UserProfile
) : Parcelable

@JsonClass(generateAdapter = true)
@Parcelize
data class UserProfile(
    @SerializedName("address")
    var address: String,
    @SerializedName("alt_email")
    var altEmail: String,
    @SerializedName("alt_mobile")
    var altMobile: String,
    @SerializedName("background_image")
    var backgroundImage: String,
    @SerializedName("birth_date")
    var birthDate: String,
    @SerializedName("city")
    var city: String,
    @SerializedName("email")
    var email: String,
    @SerializedName("equipment_use")
    var equipmentUse: String,
    @SerializedName("experiance")
    var experiance: Int,
    @SerializedName("flat_no")
    var flatNo: String,
    @SerializedName("gender")
    var gender: String,
    @SerializedName("gps_location")
    var gpsLocation: GpsLocation,
    @SerializedName("_id")
    var id: String,
    @SerializedName("landmark")
    var landmark: String,
    @SerializedName("language_know")
    var languageKnow: List<String>,
    @SerializedName("mobile")
    var mobile: Long,
    @SerializedName("my_self")
    var mySelf: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("pincode")
    var pincode: Int,
    @SerializedName("profession")
    var profession: String,
    @SerializedName("profile_image")
    var profileImage: String,
    @SerializedName("rates")
    var rates: Int,
    @SerializedName("state")
    var state: String,
    @SerializedName("studio_name")
    var studioName: String
) : Parcelable {
    @JsonClass(generateAdapter = true)
    @Parcelize
    data class GpsLocation(
        @SerializedName("_id")
        var id: String,
        @SerializedName("latitude")
        var latitude: Int,
        @SerializedName("longitude")
        var longitude: Int
    ) : Parcelable
}