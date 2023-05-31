package com.photoshooto.domain.model.all_employees_model

import com.google.gson.annotations.SerializedName


data class AllEmployeesResponse (

  @SerializedName("success" ) var success : Boolean? = null,
  @SerializedName("data"    ) var data    : Data?    = Data(),
  @SerializedName("message" ) var message : String?  = null

)