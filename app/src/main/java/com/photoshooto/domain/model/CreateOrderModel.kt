package com.photoshooto.domain.model


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CreateOrderModel(
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
    data class Data(
        @SerializedName("cancel_reason")
        @Expose
        val cancelReason: List<Any?>?,
        @SerializedName("coupon_code")
        @Expose
        val couponCode: String?,
        @SerializedName("created_at")
        @Expose
        val createdAt: String?,
        @SerializedName("deliver_address")
        @Expose
        val deliverAddress: String?,
        @SerializedName("delivery_address")
        @Expose
        val deliveryAddress: String?,
        @SerializedName("_id")
        @Expose
        val _id: String?,
        @SerializedName("id")
        @Expose
        val id: String?,
        @SerializedName("order_details")
        @Expose
        val orderDetails: OrderDetails?,
        @SerializedName("order_status")
        @Expose
        val orderStatus: String?,
        @SerializedName("project_id")
        @Expose
        val projectId: String?,
        @SerializedName("qrcode_id")
        @Expose
        val qrcodeId: String?,
        @SerializedName("standee_details")
        @Expose
        val standeeDetails: List<StandeeDetail?>?,
        @SerializedName("status")
        @Expose
        val status: String?,
        @SerializedName("tracker")
        @Expose
        val tracker: List<Any?>?,
        @SerializedName("tshirt_details")
        @Expose
        val tshirtDetails: List<Any?>?,
        @SerializedName("type")
        @Expose
        val type: String?,
        @SerializedName("updated_at")
        @Expose
        val updatedAt: String?,
        @SerializedName("user_id")
        @Expose
        val userId: String?,
        @SerializedName("__v")
        @Expose
        val v: Int?
    ) {
        data class OrderDetails(
            @SerializedName("coupon_saving")
            @Expose
            val couponSaving: Int?,
            @SerializedName("gst_tax")
            @Expose
            val gstTax: Double?,
            @SerializedName("_id")
            @Expose
            val id: String?,
            @SerializedName("shipping")
            @Expose
            val shipping: Double?,
            @SerializedName("sub_total")
            @Expose
            val subTotal: Double?,
            @SerializedName("total_amount")
            @Expose
            val totalAmount: Double?,
            @SerializedName("total_units")
            @Expose
            val totalUnits: Double?
        )

        data class StandeeDetail(
            @SerializedName("alternate_contact_number")
            @Expose
            val alternateContactNumber: String?,
            @SerializedName("alternate_number")
            @Expose
            val alternateNumber: String?,
            @SerializedName("contact_number")
            @Expose
            val contactNumber: String?,
            @SerializedName("description")
            @Expose
            val description: String?,
            @SerializedName("discount")
            @Expose
            val discount: Int?,
            @SerializedName("height")
            @Expose
            val height: String?,
            @SerializedName("_id")
            @Expose
            val id: String?,
            @SerializedName("images")
            @Expose
            val images: List<String?>?,
            @SerializedName("photographer_id")
            @Expose
            val photographerId: String?,
            @SerializedName("price")
            @Expose
            val price: Int?,
            @SerializedName("quantity")
            @Expose
            val quantity: Int?,
            @SerializedName("studio_address")
            @Expose
            val studioAddress: String?,
            @SerializedName("studio_logo")
            @Expose
            val studioLogo: List<String?>?,
            @SerializedName("studio_name")
            @Expose
            val studioName: String?,
            @SerializedName("studio_tagline")
            @Expose
            val studioTagline: String?,
            @SerializedName("type")
            @Expose
            val type: String?,
            @SerializedName("weight")
            @Expose
            val weight: String?,
            @SerializedName("width")
            @Expose
            val width: String?
        )
    }
}