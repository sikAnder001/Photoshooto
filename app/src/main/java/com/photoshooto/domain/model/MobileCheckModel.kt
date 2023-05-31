package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MobileCheckModel(
    val employee: Employee? = null,
    val message: String = "",
//    val status: Boolean?=null,
    val success: Boolean? = false,
    val token: String = "",

    val employeeType: String? = null
)

@JsonClass(generateAdapter = true)
data class Employee(
    val activities: String? = null,
    val created_at: String? = null,
    val designation: String? = null,
    val email: String? = null,
    val id: Int? = null,
    val mobileNumber: String? = null,
    val name: String? = null,
    val updated_at: String? = null
)