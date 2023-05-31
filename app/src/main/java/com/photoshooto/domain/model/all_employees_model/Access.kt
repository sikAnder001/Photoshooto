package com.photoshooto.domain.model.all_employees_model

import com.google.gson.annotations.SerializedName


data class Access (

  @SerializedName("permission" ) var permission : String? = null,
  @SerializedName("entity"     ) var entity     : String? = null

)