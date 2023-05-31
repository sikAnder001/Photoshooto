package com.photoshooto.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UpdateProfileModel(
    var category: String? = null,
    var background_image: String? = null,
    var profile_image: String? = null,
    var name: String? = null,
    var email: String? = null,
    var gender: String? = null,
    var birth_date: String? = null,
    var profession: String? = null,
    var studio_name: String? = null,
    var equipment_use: String? = null,
    var experience: String? = null,
    var mobile: String? = null,
    var flat_no: String? = null,
    var address: String? = null,
    var landmark: String? = null,
    var pincode: String? = null,
    var city: String? = null,
    var state: String? = null,
    var attachments: List<FileUpload>? = null
)

