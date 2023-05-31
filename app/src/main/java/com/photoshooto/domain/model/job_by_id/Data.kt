package com.photoshooto.domain.model.job_by_id

import com.google.gson.annotations.SerializedName
import com.photoshooto.domain.model.spam_model.List


data class Data(

    @SerializedName("_id") var _Id: String? = null,
    @SerializedName("id") var id: String? = null,
    @SerializedName("address") var address: List.JobDetails.Address? = null,
    @SerializedName("book_timings") var bookTimings: BookTimings? = BookTimings(),
    @SerializedName("category") var category: String? = null,
    @SerializedName("city") var city: ArrayList<String> = arrayListOf(),
    @SerializedName("created_at") var createdAt: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("end_date") var endDate: String? = null,
    @SerializedName("end_time") var endTime: String? = null,
    @SerializedName("event_type") var eventType: String? = null,
    @SerializedName("is_deleted") var isDeleted: Int? = null,
    @SerializedName("min_work_hours") var minWorkHours: Int? = null,
    @SerializedName("mobile") var mobile: ArrayList<Long> = arrayListOf(),
    @SerializedName("number_of_photographers") var numberOfPhotographers: Int? = null,
    @SerializedName("other_equipments") var otherEquipments: ArrayList<String> = arrayListOf(),
    @SerializedName("photo_camera_use") var photoCameraUse: ArrayList<String> = arrayListOf(),
    @SerializedName("photography_type") var photographyType: String? = null,
    @SerializedName("pin_code") var pinCode: ArrayList<String> = arrayListOf(),
    @SerializedName("pricing") var pricing: Int? = null,
    @SerializedName("start_date") var startDate: String? = null,
    @SerializedName("start_time") var startTime: String? = null,
    @SerializedName("status") var status: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("type") var type: String? = null,
    @SerializedName("updated_at") var updatedAt: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("video_camera_use") var videoCameraUse: ArrayList<String> = arrayListOf(),
    @SerializedName("is_verified") var isVerified: Boolean? = null,
    @SerializedName("feedback") var feedback: ArrayList<Feedback> = arrayListOf(),
    @SerializedName("plans") var plans: ArrayList<String> = arrayListOf(),
//    @SerializedName("favorite") var favorite: ArrayList<String> = arrayListOf(),
    @SerializedName("user_profile") var userProfile: UserProfile? = UserProfile(),
    @SerializedName("is_favorite") var isFavorite: Boolean? = null,
    @SerializedName("share_link") var shareLink: String? = null,
    @SerializedName("available_slots") var availableSlots: ArrayList<String> = arrayListOf(),
    @SerializedName("booked_slots") var bookedSlots: ArrayList<ArrayList<String>> = arrayListOf(),
    @SerializedName("report_count") var reportCount: Int? = null

)