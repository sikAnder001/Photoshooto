package com.photoshooto.domain.model.save_report_model

import com.google.gson.annotations.SerializedName


data class SaveSpamReportResponse (

  @SerializedName("success" ) var success : Boolean? = null,
  @SerializedName("data"    ) var data    : Data?    = Data(),
  @SerializedName("message" ) var message : String?  = null

)