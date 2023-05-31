package com.photoshooto.domain.model.jobmodel

import com.google.gson.annotations.SerializedName


data class Timings(

    @SerializedName("date") var date: String? = null,
    @SerializedName("time") var time: String? = null

)