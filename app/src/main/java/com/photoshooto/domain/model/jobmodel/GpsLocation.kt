package com.photoshooto.domain.model.jobmodel

import com.google.gson.annotations.SerializedName


data class GpsLocation(

    @SerializedName("latitude") var latitude: Int? = null,
    @SerializedName("longitude") var longitude: Int? = null,
    @SerializedName("_id") var Id: String? = null

)