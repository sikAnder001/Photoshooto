package com.photoshooto.domain.model

import com.google.gson.annotations.SerializedName

data class PostJobPRQ(
    @SerializedName("type")
    val type: String = "",
    @SerializedName("job_type ")
    val jobType: String = "",
    @SerializedName("title")
    val title: String = "",
    @SerializedName("mobile")
    val mobile: ArrayList<Long>,
    @SerializedName("photography_type")
    val photographyType: String,
    @SerializedName("event_type")
    val eventType: String,
    @SerializedName("start_date")
    val startDate: String = "",
    @SerializedName("start_time")
    val startTime: String = "",
    @SerializedName("end_date")
    val endDate: String = "",
    @SerializedName("end_time")
    val endTime: String = "",
    @SerializedName("city")
    val city: ArrayList<String>,
    @SerializedName("photo_camera_use")
    val cameraUse: ArrayList<String>,
    @SerializedName("video_camera_use")
    val videoUse: ArrayList<String>,
    @SerializedName("other_equipments")
    val otherUse: ArrayList<String>,
    @SerializedName("number_of_photographers")
    val numberOfPhotographers: Int = 1,
    @SerializedName("address")
    val address: String = "",
    @SerializedName("description")
    val description: String = "",
    @SerializedName("latitude")
    val latitude: Double = 0.0,
    @SerializedName("longitude")
    val longitude: Double = 0.0
)
