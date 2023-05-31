package com.photoshooto.domain.model.reportedListModel

import com.google.gson.annotations.SerializedName


data class ReportedListResponse(

    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("data") var data: Data? = Data(),
    @SerializedName("message") var message: String? = null

)