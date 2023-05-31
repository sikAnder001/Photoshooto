package com.photoshooto.domain

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class AllStatusModel(
    val message: String,
    val success: Boolean
) : Serializable