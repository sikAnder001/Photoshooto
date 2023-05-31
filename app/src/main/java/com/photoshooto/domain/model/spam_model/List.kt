package com.photoshooto.domain.model.spam_model

import com.google.gson.annotations.SerializedName


data class List(

    @SerializedName("id") var id: String? = null,
    @SerializedName("job_id") var jobId: String? = null,
    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("report_count") var reportCount: Int? = null,
    @SerializedName("user_profile") var userProfile: UserProfile? = UserProfile(),
    @SerializedName("job_details") var jobDetails: JobDetails? = null

) {
    data class JobDetails(
        val id: String? = null,
        val address: Address? = null,

//    @Json(name = "book_timings")
//    val book_timings: BookTimings? = null,

        val category: String? = null,
        val city: ArrayList<String>? = null,

        val created_at: String? = null,

        val description: String? = null,

        val end_date: String? = null,

        val end_time: String? = null,

        val event_type: String? = null,

        val is_deleted: Long? = null,

        val min_work_hours: Long? = null,

        val number_of_photographers: Long? = null,

        val other_equipments: ArrayList<String>? = null,

        val photo_camera_use: ArrayList<String>? = null,

        val photography_type: String? = null,

        val pin_code: ArrayList<String>? = null,

        val pricing: Long? = null,

        val start_date: String? = null,

        val start_time: String? = null,

        val status: String? = null,
        val title: String? = null,
        val type: String? = null,

        val updated_at: String? = null,

        val user_id: String? = null,

        val video_camera_use: ArrayList<String>? = null,

        val is_verified: Boolean? = null,

        val plan_type: String? = null,

        val event: String? = null
    ) {
        data class Address(
            val location: String? = null,
            val latitude: Double? = null,
            val longitude: Double? = null,
            val _id: String? = null,
        )
    }
}

