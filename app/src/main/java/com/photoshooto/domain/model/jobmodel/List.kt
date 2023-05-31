package com.photoshooto.domain.model.jobmodel

import com.google.gson.annotations.SerializedName
import com.photoshooto.domain.model.spam_model.List


data class List(

    @SerializedName("_id") var _id: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("address") var address: List.JobDetails.Address? = null,
    @SerializedName("alternet_phone") var alternetPhone: Long? = null,
    @SerializedName("book_timings") var bookTimings: BookTimings? = BookTimings(),
    @SerializedName("camera_use") var cameraUse: String? = null,
    @SerializedName("category") var category: String? = null,
    @SerializedName("city") var city: ArrayList<String> = arrayListOf(),
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("end_date") var endDate: String? = null,
    @SerializedName("event") var event: String? = null,
    @SerializedName("event_type") var eventType: String? = null,
    @SerializedName("min_work_hours") var minWorkHours: Int? = null,
    @SerializedName("mobile") var mobile: ArrayList<String> = arrayListOf(),
    @SerializedName("number_of_photographers") var numberOfPhotographers: Int? = null,
    @SerializedName("other_equipments") var otherEquipments: ArrayList<String> = arrayListOf(),
    @SerializedName("photo_camera_use") var photoCameraUse: ArrayList<String> = arrayListOf(),
    @SerializedName("photography_type") var photographyType: String? = null,
    @SerializedName("pin_code") var pinCode: ArrayList<String> = arrayListOf(),
    @SerializedName("pricing") var pricing: Int? = null,
    @SerializedName("start_date") var startDate: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("timings") var timings: ArrayList<Timings> = arrayListOf(),
    @SerializedName("title") var title: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("video_camera_use") var videoCameraUse: ArrayList<String> = arrayListOf(),
    @SerializedName("user_profile") var userProfile: UserProfile? = UserProfile()

)