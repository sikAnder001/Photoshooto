package com.photoshooto.domain.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.photoshooto.ui.job.utility.AppConstant


open class BasePRQ(
    @SerializedName(AppConstant.authKey) var vAuthKey: String? = "",
    @SerializedName(AppConstant.page) var page: String? = "",
    @SerializedName(AppConstant.search_term) var search_term: String? = "",
    @SerializedName(AppConstant.language) var language: String? = ""
)

open class BaseResponse(
    @Expose
    @SerializedName("success")
    var success: Boolean = false,
    @Expose
    @SerializedName("message")
    var message: String? = ""
) {
    fun isSuccess(): Boolean {
        return success
    }
}