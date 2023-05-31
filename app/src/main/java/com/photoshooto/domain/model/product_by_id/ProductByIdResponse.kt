package  com.photoshooto.domain.model.product_by_id

import com.google.gson.annotations.SerializedName


data class ProductByIdResponse(

    @SerializedName("success") var success: Boolean? = null,
    @SerializedName("data") var data: Data? = Data(),
    @SerializedName("message") var message: String? = null

)