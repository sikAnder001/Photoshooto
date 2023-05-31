package com.photoshooto.domain.model

import com.google.gson.annotations.SerializedName

data class LocationFilter(
    @SerializedName("select")
    var select: Boolean = false,

    @SerializedName("name ")
    var name: String = "",
)
