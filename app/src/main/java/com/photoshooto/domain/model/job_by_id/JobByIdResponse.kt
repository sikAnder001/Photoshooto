package com.photoshooto.domain.model.job_by_id

import com.google.gson.annotations.SerializedName


data class JobByIdResponse(

    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("data") var data: ArrayList<Data> = arrayListOf(),
    @SerializedName("message") var message: String? = null

)