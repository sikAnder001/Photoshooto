package com.photoshooto.domain.model.spam_model

import com.google.gson.annotations.SerializedName


data class SpamReportResponse(

    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("data") var data: Data? = Data(),
    @SerializedName("message") var message: String? = null

)