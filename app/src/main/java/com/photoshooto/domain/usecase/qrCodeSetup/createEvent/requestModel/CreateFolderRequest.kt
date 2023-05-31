package com.photoshooto.domain.usecase.qrCodeSetup.createEvent.requestModel

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class CreateFolderRequest(
    var name: String? = null
)

