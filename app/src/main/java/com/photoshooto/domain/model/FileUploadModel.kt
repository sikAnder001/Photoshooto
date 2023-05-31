package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FileUploadModel(
    val data: List<FileUpload>,
    val message: String,
    val rediskey: String,
    val success: Boolean
)

@JsonClass(generateAdapter = true)
data class FileUpload(
    val file_name: String? = null,
    val file_path: String? = null,
    val file_size: Int,
    val file_type: String? = null,
    val id: String? = null,
    val type: String? = null,
    val user_id: String? = null,
    val category: String? = null
)


