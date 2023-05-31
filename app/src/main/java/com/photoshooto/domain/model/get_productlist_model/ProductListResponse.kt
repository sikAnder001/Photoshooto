package com.photoshooto.domain.model.get_productlist_model

import com.google.gson.annotations.SerializedName


data class ProductListResponse(

    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("data") var data: Data? = Data(),
    @SerializedName("message") var message: String? = null

)