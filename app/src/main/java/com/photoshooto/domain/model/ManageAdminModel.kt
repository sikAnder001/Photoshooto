package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class ManageAdminModel(
    val data: ArrayList<AdminUser>? = null,
    val message: String? = null,
    var success: Boolean? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class AdminUser(
    val profileImage: Int? = null,
    val name: String? = null,
    val code: String? = null
) : Serializable
