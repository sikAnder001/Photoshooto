package com.photoshooto.domain.model.reportedListModel

import com.google.gson.annotations.SerializedName


data class Data(

    @SerializedName("list") var list: ArrayList<ReportedList> = arrayListOf(),
    @SerializedName("page") var page: Int? = null,
    @SerializedName("next_page") var nextPage: Int? = null

)