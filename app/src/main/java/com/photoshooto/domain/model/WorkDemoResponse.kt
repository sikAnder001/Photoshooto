package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WorkDemoResponse(
    val data: List<WorkDemoItem>, val message: String, val success: Boolean
)

@JsonClass(generateAdapter = true)
data class WorkDemoItem(
    val _id: String,
    val category: String,
    val created_at: String,
    val file_height: String,
    val file_name: String,
    val file_path: String,
    val file_size: Int,
    val file_type: String,
    val file_width: String,
    val id: String,
    val is_deleted: Boolean,
    val s3_url: String,
    val status: String,
    val type: String,
    val updated_at: String,
    val user_id: String
)
