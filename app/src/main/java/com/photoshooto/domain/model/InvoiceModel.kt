package com.photoshooto.domain.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InvoiceModel(
    @SerializedName("data")
    @Expose
    val data: Data?,
    @SerializedName("message")
    @Expose
    val message: String?,
    @SerializedName("success")
    @Expose
    val success: Boolean?
) {
    @JsonClass(generateAdapter = true)
    data class Data(
        @SerializedName("invoice_pdf")
        @Expose
        val invoicePdf: String?,
        @SerializedName("order_id")
        @Expose
        val orderId: String?
    )
}