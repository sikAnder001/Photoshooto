package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginTimePhotoResponse(
    val data: LoginData? = null,
    val message: String? = "",
    val success: Boolean? = false,
    val status: Boolean? = false,
)

@JsonClass(generateAdapter = true)
data class LoginData(
    val data: DataX? = null,
    val employeetrackings: List<Employeetracking>? = null,
    val token: String? = ""
)

@JsonClass(generateAdapter = true)
data class Employeetracking(
    val created_at: String? = "",
    val employeeid: String? = "",
    val id: Int? = 0,
    val loginphoto: String? = "",
    val logintime: String? = "",
    val logoutphoto: String? = "",
    val logouttime: String? = "",
    val updated_at: String? = ""
)

@JsonClass(generateAdapter = true)
data class DataX(
    val created_at: String? = "",
    val employeeid: String? = "",
    val id: Int,
    val loginphoto: String? = "",
    val logintime: String? = "",
    val updated_at: String? = ""
)


