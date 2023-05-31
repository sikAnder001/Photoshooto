package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class NewRequestModel(
    val data: NewRequestData? = null,
    val message: String? = null,
    var success: Boolean? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class NewRequestData(
    val requestList: ArrayList<RequestedUser>? = null,
    val filterList: ArrayList<RequestedFilterItem>? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class RequestedUser(
    val profileImage: Int? = null,
    val name: String? = null,
    val id: String? = null,
    val designation: String? = null,
    val location: String? = null,
    val requestTime: String? = null,
    val date1: String? = null,
    val date2: String? = null
) : Serializable

@JsonClass(generateAdapter = true)
data class RequestedFilterItem(
    val name: String? = null,
    var isSelected: Boolean = false
) : Serializable
