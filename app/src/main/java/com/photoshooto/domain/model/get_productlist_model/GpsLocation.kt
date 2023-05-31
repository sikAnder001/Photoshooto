package com.photoshooto.domain.model.get_productlist_model

import com.google.gson.annotations.SerializedName


data class GpsLocation(

    @SerializedName("latitude") var latitude: Int? = null,
    @SerializedName("longitude") var longitude: Int? = null,
    @SerializedName("_id") var Id: String? = null

)