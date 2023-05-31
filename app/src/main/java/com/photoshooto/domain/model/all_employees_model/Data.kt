package com.photoshooto.domain.model.all_employees_model

import com.google.gson.annotations.SerializedName


data class Data (

  @SerializedName("list"      ) var list     : ArrayList<List> = arrayListOf(),
  @SerializedName("page"      ) var page     : Int?            = null,
  @SerializedName("next_page" ) var nextPage : Int?            = null

)