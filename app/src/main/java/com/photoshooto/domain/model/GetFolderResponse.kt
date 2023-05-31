package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class GetFolderResponse(
    val data: GetFolderModel? = null,
    val message: String? = null,
    val success: Boolean? = false
)

@JsonClass(generateAdapter = true)
data class GetFolderModel(
    val list: List<FolderModel>? = null,
    val page: String? = null,
    val next_page: String? = null
)

@JsonClass(generateAdapter = true)
data class FolderModel(
    var _id: String? = "",
    var id: String? = "",
    var name: String? = "",
    var user_id: List<String?>? = listOf(),
    var created_at: String? = "",
    var is_deleted: Boolean? = false,
    var merge_photos: MergePhotos? = MergePhotos(),
    var photo_id: List<String?>? = listOf(),
    var rejected_photo_ids: List<String?>? = listOf(),
    var selected_photo_ids: List<String?>? = listOf(),
    var updated_at: String? = ""
)

@JsonClass(generateAdapter = true)
data class MergePhotos(
    var selected_photos: List<String?>? = listOf(),
    var rejected_photos: List<String?>? = listOf(),
    var _id: String? = "",
    var conflict_photos: List<String?>? = listOf()
)


