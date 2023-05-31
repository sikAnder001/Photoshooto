package com.photoshooto.domain.model.jobmodel

import com.google.gson.annotations.SerializedName


data class Data(

    @SerializedName("list") var list: ArrayList<List> = arrayListOf(),
    @SerializedName("page") var page: Int? = null,
    @SerializedName("next_page") var nextPage: Int? = null

)