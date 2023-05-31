package com.photoshooto.domain.model.jobmodel

import com.google.gson.annotations.SerializedName


data class BookTimings(

    @SerializedName("user_id") var userId: String? = null,
    @SerializedName("_id") var Id: String? = null

)